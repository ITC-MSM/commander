#!/usr/bin/env python3
"""
Bootstrap a set's backlog: Scryfall dump -> `backlog/sets/<set-name>/cards.md`, a
`mechanics.md` draft, an oracle-text worksheet, and (optionally) the `definitions/<dir>/`
Kotlin scaffold.

This is the mechanical half of the `create-backlog-for-set` skill. It does the parts that
must be exact — resolving the set, downloading every printing, partitioning draft vs.
extras the way `scripts/card-status` does, grouping by colour, ticking cards that already
have a `CardDefinition`, counting. It deliberately does NOT do the parts that need
judgement: the mechanics descriptions, the engine-support verdicts, `block` /
`basicLandsFallback` on the scaffolded set object. Those are the agent's job, and the
files this writes are marked where they need it.

Cache: the full dump lives at `~/.cache/scryfall/_setdump-<code>.json` — the same directory
`scripts/card-status`, `:mtgish-tooling` and Argentum Assay use, under a `_setdump-` prefix
that can't collide with a set code (Assay's own entries use `_bulk-` / `_setlist-`). A set
released more than 30 days ago is frozen, so its dump is never re-fetched without
`--refresh`; a set still in spoiler season re-fetches every run.

Usage:
  bootstrap-set.py DRK                      # by code
  bootstrap-set.py "The Dark"               # or by name
  bootstrap-set.py DRK --slug the-dark      # override the backlog directory name
  bootstrap-set.py EVE --no-scaffold        # backlog files only
  bootstrap-set.py TMP --refresh --force    # re-fetch, overwrite existing backlog files
"""

from __future__ import annotations

import argparse
import json
import re
import sys
import time
import unicodedata
import urllib.error
import urllib.parse
import urllib.request
from collections import defaultdict
from datetime import date, timedelta
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[3]
sys.path.insert(0, str(REPO_ROOT / "scripts"))

from card_exclusions import load_exclusions  # noqa: E402
from set_dirs import definitions_roots, dir_for_codes, set_dir_paths  # noqa: E402

CACHE_ROOT = Path.home() / ".cache" / "scryfall"
SCRYFALL_BASE = "https://api.scryfall.com"
USER_AGENT = "argentum-engine-backlog-bootstrap/1.0"
REQUEST_DELAY_SEC = 0.15
REQUEST_TIMEOUT_SEC = 30
REFRESH_WINDOW_DAYS = 30
DUMP_SCHEMA_VERSION = 1

CARD_DSL_RE = re.compile(r'\b(?:card|basicLand)\(\s*"([^"]+)"')
PRINTING_NAME_RE = re.compile(r'\bname\s*=\s*"([^"]+)"')
ABILITY_WORD_RE = re.compile(r"^([A-Z][A-Za-z'\-]*(?: [a-z'\-]+){0,3}) —", re.MULTILINE)

# `con` is a DOS device name and can't be a directory on Windows; Conflux lives in `conflux/`.
RESERVED_DIR_NAMES = {
    "con": "conflux",
    "prn": "prn-set",
    "aux": "aux-set",
    "nul": "nul-set",
    **{f"com{i}": f"com{i}-set" for i in range(1, 10)},
    **{f"lpt{i}": f"lpt{i}-set" for i in range(1, 10)},
}

COLOR_SECTIONS = [
    ("White", "W"),
    ("Blue", "U"),
    ("Black", "B"),
    ("Red", "R"),
    ("Green", "G"),
]


# --------------------------------------------------------------------------- Scryfall


def scryfall_get(url: str, *, max_retries: int = 5) -> dict:
    delay = 1.0
    for attempt in range(max_retries):
        req = urllib.request.Request(
            url, headers={"User-Agent": USER_AGENT, "Accept": "application/json"}
        )
        try:
            with urllib.request.urlopen(req, timeout=REQUEST_TIMEOUT_SEC) as resp:
                time.sleep(REQUEST_DELAY_SEC)
                return json.loads(resp.read().decode("utf-8"))
        except urllib.error.HTTPError as e:
            if e.code == 429 and attempt < max_retries - 1:
                time.sleep(delay)
                delay *= 2
                continue
            raise
        except (urllib.error.URLError, TimeoutError):
            if attempt < max_retries - 1:
                time.sleep(delay)
                delay *= 2
                continue
            raise
    raise RuntimeError("unreachable")


def resolve_set(query: str) -> dict:
    """Set metadata for a code or a name. Ambiguous names exit with the candidates."""
    slug = query.strip().lower()
    if re.fullmatch(r"[a-z0-9]{3,6}", slug):
        try:
            return scryfall_get(f"{SCRYFALL_BASE}/sets/{urllib.parse.quote(slug)}")
        except urllib.error.HTTPError as e:
            if e.code != 404:
                raise
    all_sets = scryfall_get(f"{SCRYFALL_BASE}/sets").get("data", [])
    exact = [s for s in all_sets if s.get("name", "").lower() == slug]
    if len(exact) == 1:
        return exact[0]
    partial = [s for s in all_sets if slug in s.get("name", "").lower()]
    if len(partial) == 1:
        return partial[0]
    candidates = exact or partial
    if not candidates:
        sys.exit(f"bootstrap-set: no Scryfall set matches '{query}'")
    lines = "\n".join(
        f"  {s['code']}  {s['name']}  ({s.get('released_at', '?')}, {s.get('set_type', '?')})"
        for s in sorted(candidates, key=lambda s: s.get("released_at") or "")
    )
    sys.exit(
        f"bootstrap-set: '{query}' is ambiguous — re-run with one of these codes:\n{lines}"
    )


def dump_path(code: str) -> Path:
    return CACHE_ROOT / f"_setdump-{code.lower()}.json"


def is_dump_fresh(payload: dict) -> bool:
    if payload.get("_v") != DUMP_SCHEMA_VERSION:
        return False
    released = (payload.get("set") or {}).get("released_at")
    if not released:
        return False
    try:
        released_date = date.fromisoformat(released)
    except ValueError:
        return False
    # Released sets are frozen; a set in (or before) spoiler season still moves.
    return released_date < date.today() - timedelta(days=REFRESH_WINDOW_DAYS)


def load_dump(set_meta: dict, *, refresh: bool) -> list[dict]:
    """Every printing in the set, cached. `unique=prints` — one card can have several
    printings inside a single set, and `booster` has to be OR-ed across all of them."""
    code = set_meta["code"].lower()
    path = dump_path(code)
    if not refresh and path.is_file():
        try:
            cached = json.loads(path.read_text(encoding="utf-8"))
        except json.JSONDecodeError:
            cached = None
        if cached and is_dump_fresh(cached):
            return cached["data"]

    cards: list[dict] = []
    url = (
        f"{SCRYFALL_BASE}/cards/search"
        f"?q={urllib.parse.quote(f'set:{code} -is:rebalanced')}"
        f"&unique=prints&order=set"
    )
    while url:
        page = scryfall_get(url)
        cards.extend(page.get("data", []))
        url = page.get("next_page") if page.get("has_more") else None
    CACHE_ROOT.mkdir(parents=True, exist_ok=True)
    path.write_text(
        json.dumps({"_v": DUMP_SCHEMA_VERSION, "set": set_meta, "data": cards}),
        encoding="utf-8",
    )
    return cards


# --------------------------------------------------------------------------- card model


def front_face(name: str) -> str:
    return name.split(" // ", 1)[0].strip()


def face_field(card: dict, field: str) -> str:
    """A card-level field, falling back to the front face for multi-faced layouts."""
    value = card.get(field)
    if value not in (None, ""):
        return value
    faces = card.get("card_faces") or []
    if faces and isinstance(faces[0], dict):
        return faces[0].get(field) or ""
    return ""


def joined_oracle(card: dict) -> str:
    text = card.get("oracle_text")
    if text is not None:
        return text
    faces = card.get("card_faces") or []
    return "\n//\n".join(f.get("oracle_text", "") for f in faces if isinstance(f, dict))


def card_colors(card: dict) -> set[str]:
    colors = card.get("colors")
    if colors is None:
        faces = card.get("card_faces") or []
        colors = sorted({c for f in faces for c in (f.get("colors") or [])})
    return set(colors or [])


def section_for(card: dict) -> str:
    """The cards.md heading a card belongs under — colour first, then type."""
    type_line = face_field(card, "type_line") or card.get("type_line", "")
    colors = card_colors(card)
    if len(colors) > 1:
        return "Multicolor"
    if len(colors) == 1:
        return next(name for name, sym in COLOR_SECTIONS if sym in colors)
    if "Land" in type_line:
        return "Land"
    if "Artifact" in type_line:
        return "Artifact"
    return "Colorless"


def section_order(payload_sections: set[str]) -> list[str]:
    order = [name for name, _ in COLOR_SECTIONS] + [
        "Multicolor",
        "Artifact",
        "Colorless",
        "Land",
    ]
    return [s for s in order if s in payload_sections]


class SetCards:
    """The set's canonical cards, partitioned the way `scripts/card-status` partitions them."""

    def __init__(self, printings: list[dict]) -> None:
        by_name: dict[str, list[dict]] = defaultdict(list)
        for card in printings:
            by_name[card["name"]].append(card)
        self.draft: dict[str, dict] = {}
        self.extra: dict[str, dict] = {}
        for name, group in by_name.items():
            # `booster` is a property of a printing; a card is draft if ANY of its printings
            # in this set is a booster printing.
            representative = min(
                group,
                key=lambda c: (
                    0 if c.get("booster") else 1,
                    c.get("collector_number", ""),
                ),
            )
            target = self.draft if any(c.get("booster") for c in group) else self.extra
            target[front_face(name)] = representative

    @property
    def all(self) -> dict[str, dict]:
        return {**self.draft, **self.extra}


def scan_implemented(cards_dir: Path | None) -> set[str]:
    """Front-face names that already have a `CardDefinition` or a `Printing` row."""
    names: set[str] = set()
    if cards_dir is None or not cards_dir.is_dir():
        return names
    for kt in cards_dir.glob("*.kt"):
        text = kt.read_text(encoding="utf-8", errors="replace")
        names.update(CARD_DSL_RE.findall(text))
        names.update(PRINTING_NAME_RE.findall(text))
    return {front_face(n) for n in names}


# --------------------------------------------------------------------------- scaffold


def kotlin_object_name(display_name: str) -> str:
    ascii_name = (
        unicodedata.normalize("NFKD", display_name).encode("ascii", "ignore").decode()
    )
    # Drop apostrophes before splitting, or "Urza's Saga" derives `UrzaSSagaSet` instead of the
    # repo's `UrzasSagaSet`; the possessive is part of the word, not a separator.
    words = re.findall(r"[A-Za-z0-9]+", ascii_name.replace("'", "").replace("\u2019", ""))
    ident = "".join(w[:1].upper() + w[1:] for w in words)
    if not ident.endswith("Set"):  # "Arena Beginner Set" is already one
        ident += "Set"
    if ident[:1].isdigit():
        sys.exit(
            f"bootstrap-set: '{display_name}' produces the invalid Kotlin identifier '{ident}' — "
            "pass --object-name to choose one (e.g. TenthEditionSet)"
        )
    return ident


def era_module_for(year: int) -> Path:
    """The `mtg-sets/<era>` module owning a release year. Era ranges are fixed; a year past
    the newest module needs a new module in settings.gradle.kts, which is not this script's job."""
    modules: list[tuple[int, int, Path]] = []
    for root in definitions_roots():
        # root is `<module>/src/main/kotlin/com/wingedsheep/mtg/sets/definitions`
        module = next(
            p for p in root.parents if p.name != "src" and (p / "src").is_dir()
        )
        name = module.name
        if name == "core":
            continue
        m = re.fullmatch(r"(\d{4})(?:-(\d{4}))?", name)
        if not m:
            continue
        start = int(m.group(1))
        end = int(m.group(2) or m.group(1))
        modules.append((start, end, module))
    for start, end, module in sorted(modules):
        if start <= year <= end:
            return module
    newest = max(modules)[2].name if modules else "?"
    sys.exit(
        f"bootstrap-set: release year {year} is past the newest era module ({newest}). "
        "Add the era to settings.gradle.kts and mtg-sets/ first — see AGENTS.md 'Module layout'."
    )


def scaffold_set(set_meta: dict, dir_name: str, object_name: str) -> tuple[Path, bool]:
    """Write `definitions/<dir>/<Object>.kt` + an empty `cards/`. Returns (path, created)."""
    existing = set_dir_paths().get(dir_name)
    year = int((set_meta.get("released_at") or "0000")[:4])
    root = (
        existing.parent
        if existing
        else era_module_for(year)
        / ("src/main/kotlin/com/wingedsheep/mtg/sets/definitions")
    )
    set_dir = root / dir_name
    # A scaffolded set is identified by *any* `*Set.kt` in its directory, not by the name this
    # script would derive. The repo shortens long titles (`LostCavernsOfIxalanSet` for "The Lost
    # Caverns of Ixalan"), so keying on the derived name would drop a second `MtgSet` object into
    # the same package and `MtgSetCatalog` would discover both.
    already = next((p for p in sorted(set_dir.glob("*Set.kt")) if p.is_file()), None)
    if already is not None:
        return already, False
    kt = set_dir / f"{object_name}.kt"
    (set_dir / "cards").mkdir(parents=True, exist_ok=True)
    package = f"com.wingedsheep.mtg.sets.definitions.{dir_name}"
    released = set_meta.get("released_at")
    kt.write_text(
        f"""package {package}

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * {set_meta["name"]} ({released[:4] if released else "?"})
 *
 * Scaffolded to hold the canonical [CardDefinition]s of cards whose earliest real printing is
 * {set_meta["name"]}, with later sets contributing reprint [Printing] rows. Intentionally
 * incomplete relative to the official set.
 *
 * Set Code: {set_meta["code"].upper()}
 * Release Date: {released or "unknown"}
 */
object {object_name} : MtgSet {{

    override val code = "{set_meta["code"].upper()}"
    override val displayName = "{set_meta["name"]}"
    override val releaseDate = {f'"{released}"' if released else "null"}
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {{
        CardDiscovery.findIn(CARDS_PACKAGE)
    }}

    override val basicLands: List<CardDefinition> by lazy {{
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }}

    override val printings: List<Printing> by lazy {{
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }}

    private const val CARDS_PACKAGE = "{package}.cards"
}}
""",
        encoding="utf-8",
    )
    return kt, True


# --------------------------------------------------------------------------- writers


def format_release(released: str | None) -> str:
    if not released:
        return "unknown"
    try:
        d = date.fromisoformat(released)
    except ValueError:
        return released
    return f"{d.strftime('%B')} {d.day}, {d.year}"


def card_line(name: str, implemented: set[str], exclusions: dict[str, str]) -> str:
    if name in implemented:
        return f"- [x] {name}"
    if name in exclusions:
        # `[-]` is card-status's "not planned" marker: off the denominator, still visible.
        return f"- [-] {name}  (not planned: {exclusions[name]})"
    return f"- [ ] {name}"


def write_cards_md(
    path: Path,
    set_meta: dict,
    cards: SetCards,
    implemented: set[str],
    exclusions: dict[str, str],
) -> tuple[int, int]:
    grouped: dict[str, list[str]] = defaultdict(list)
    for name, card in cards.draft.items():
        grouped[section_for(card)].append(name)

    countable = [n for n in cards.all if n not in exclusions or n in implemented]
    done = len([n for n in countable if n in implemented])

    rows = []
    for section in section_order(set(grouped)):
        names = grouped[section]
        rows.append((section, len(names), len([n for n in names if n in implemented])))
    if cards.extra:
        rows.append(
            (
                "Extras",
                len(cards.extra),
                len([n for n in cards.extra if n in implemented]),
            )
        )

    out: list[str] = []
    out.append(f"# {set_meta['name']} ({set_meta['code'].upper()}) - Card Checklist")
    out.append("")
    out.append(f"**Set Size:** {len(countable)} cards")
    out.append(f"**Release Date:** {format_release(set_meta.get('released_at'))}")
    out.append(f"**Implemented:** {done} / {len(countable)}")
    out.append("")
    out.append("| Section    | Total | Done |")
    out.append("|------------|-------|------|")
    for section, total, hits in rows:
        out.append(f"| {section:<10} | {total:<5} | {hits:<4} |")
    out.append("")
    out.append(
        "> Verify status anytime with `scripts/card-status --set "
        f"{set_meta['code'].upper()}` (and `--list`). That command's count is authoritative — "
        "keep this file's `Implemented:` line in sync (`just fix-backlog`) as boxes are checked. "
        "The set's mechanics are catalogued in [`mechanics.md`](mechanics.md)."
    )
    out.append("")
    out.append("---")
    for section in section_order(set(grouped)):
        out.append("")
        out.append(f"### {section}")
        for name in sorted(grouped[section]):
            out.append(card_line(name, implemented, exclusions))
    if cards.extra:
        out.append("")
        out.append("### Extras")
        out.append("")
        out.append(
            "> Not in boosters — starter decks, promos, bonus sheets. Counted separately by "
            "`scripts/card-status`."
        )
        out.append("")
        for name in sorted(cards.extra):
            out.append(card_line(name, implemented, exclusions))
    out.append("")
    path.write_text("\n".join(out), encoding="utf-8")
    return done, len(countable)


def tally_mechanics(
    cards: SetCards,
) -> tuple[list[tuple[str, list[str]]], list[tuple[str, list[str]]]]:
    """(keyword, cards) and (ability word, cards), each ordered by card count then name."""
    keywords: dict[str, list[str]] = defaultdict(list)
    ability_words: dict[str, list[str]] = defaultdict(list)
    for name, card in sorted(cards.all.items()):
        for kw in card.get("keywords") or []:
            keywords[kw].append(name)
        for match in ABILITY_WORD_RE.finditer(joined_oracle(card)):
            word = match.group(1)
            if word not in (card.get("keywords") or []):
                ability_words[word].append(name)

    def ordered(d: dict[str, list[str]]) -> list[tuple[str, list[str]]]:
        return sorted(d.items(), key=lambda kv: (-len(kv[1]), kv[0]))

    return ordered(keywords), ordered(ability_words)


def plural_cards(n: int) -> str:
    return "1 card" if n == 1 else f"{n} cards"


def write_mechanics_md(path: Path, set_meta: dict, cards: SetCards) -> int:
    keywords, ability_words = tally_mechanics(cards)
    code = set_meta["code"].upper()
    out: list[str] = []
    out.append(f"# {set_meta['name']} ({code}) — Mechanics")
    out.append("")
    out.append(
        f"**DRAFT — needs a pass.** Generated from the Scryfall dump of {len(cards.all)} cards "
        "(`keywords` + ability-word headers only). Every entry below still needs its description "
        "written, its engine-support verdict checked against the SDK, and the mechanics Scryfall "
        "does not name — the set's themes — added by reading the oracle text. Delete this "
        "paragraph once that pass is done."
    )
    out.append("")
    out.append(
        "A box is ticked when the engine already models the mechanic (an SDK primitive exists "
        "and a card in the corpus uses it). An unticked box is `add-feature` work that blocks "
        "the cards listed under it."
    )
    out.append("")
    out.append("---")
    out.append("")
    out.append("## Keyword mechanics")
    if not keywords:
        out.append("")
        out.append("_Scryfall names no keywords in this set._")
    for keyword, names in keywords:
        out.append("")
        out.append(f"### - [ ] {keyword} ({plural_cards(len(names))})")
        out.append("")
        out.append("_Description: what it does, and the CR rule that defines it._")
        out.append("")
        out.append("**Engine support:** _unverified_")
        out.append("")
        out.append(f"Cards: {', '.join(names)}")
    if ability_words:
        out.append("")
        out.append("## Ability words / named triggers")
        out.append("")
        out.append(
            "_Harvested from `Word —` headers in the oracle text. Some are real ability words, "
            "some are flavour lines or level-up rows — prune before filling in._"
        )
        for word, names in ability_words:
            out.append("")
            out.append(f"### - [ ] {word} ({plural_cards(len(names))})")
            out.append("")
            out.append("_Description._")
            out.append("")
            out.append("**Engine support:** _unverified_")
            out.append("")
            out.append(f"Cards: {', '.join(names)}")
    out.append("")
    out.append("## Set themes")
    out.append("")
    out.append(
        "_Unnamed mechanics the set leans on — the recurring templates Scryfall has no keyword "
        "for (artifact-matters, sacrifice fuel, a tribal axis, an unusual cost). Read the oracle "
        "worksheet and add one `### - [ ] Theme (N cards)` section each, in the same shape as "
        "above._"
    )
    out.append("")
    path.write_text("\n".join(out), encoding="utf-8")
    return len(keywords) + len(ability_words)


def write_worksheet(path: Path, cards: SetCards) -> None:
    """One TSV row per card — the cheapest way to read a whole set's oracle text."""
    path.parent.mkdir(parents=True, exist_ok=True)
    rows = [
        "name\tpartition\trarity\tmana_cost\ttype_line\tkeywords\treprint\toracle_text"
    ]
    for partition, group in (("draft", cards.draft), ("extra", cards.extra)):
        for name, card in sorted(group.items()):
            oracle = joined_oracle(card).replace("\t", " ").replace("\n", " | ")
            rows.append(
                "\t".join(
                    [
                        name,
                        partition,
                        card.get("rarity", ""),
                        face_field(card, "mana_cost"),
                        face_field(card, "type_line") or card.get("type_line", ""),
                        ",".join(card.get("keywords") or []),
                        "yes" if card.get("reprint") else "no",
                        oracle,
                    ]
                )
            )
    path.write_text("\n".join(rows) + "\n", encoding="utf-8")


# --------------------------------------------------------------------------- main


def main(argv: list[str]) -> int:
    ap = argparse.ArgumentParser(
        description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter
    )
    ap.add_argument("set", help='set code (DRK) or name ("The Dark")')
    ap.add_argument(
        "--slug",
        help="backlog/sets/<slug> directory name (default: the set name, kebab-cased)",
    )
    ap.add_argument(
        "--dir-name", help="definitions/<dir> name (default: the lowercase set code)"
    )
    ap.add_argument(
        "--object-name", help="Kotlin object name (default: derived from the set name)"
    )
    ap.add_argument(
        "--refresh", action="store_true", help="re-download the dump even if cached"
    )
    ap.add_argument(
        "--no-scaffold", action="store_true", help="write backlog files only"
    )
    ap.add_argument(
        "--force",
        action="store_true",
        help="overwrite existing cards.md / mechanics.md",
    )
    args = ap.parse_args(argv)

    set_meta = resolve_set(args.set)
    code = set_meta["code"].lower()
    printings = load_dump(set_meta, refresh=args.refresh)
    if not printings:
        sys.exit(f"bootstrap-set: Scryfall returned no cards for set '{code}'")
    cards = SetCards(printings)

    slug = args.slug or re.sub(r"[^a-z0-9]+", "-", set_meta["name"].lower()).strip("-")
    dir_name = args.dir_name or RESERVED_DIR_NAMES.get(code, code)
    object_name = args.object_name or kotlin_object_name(set_meta["name"])

    print(
        f"{set_meta['name']} ({code.upper()}) — {set_meta.get('released_at', '?')}, "
        f"{len(cards.draft)} draft + {len(cards.extra)} extra, {len(printings)} printings"
    )

    # --- scaffold ---------------------------------------------------------
    scaffolded_dir = dir_for_codes().get(code)
    if scaffolded_dir:
        dir_name = scaffolded_dir
    if not args.no_scaffold:
        kt, created = scaffold_set(set_meta, dir_name, object_name)
        print(
            ("scaffolded " if created else "set exists  ")
            + str(kt.relative_to(REPO_ROOT))
        )
        if created:
            print(
                "  ^ review `block` / `basicLandsFallback` / `sealedSupported` by hand"
            )
    cards_dir = set_dir_paths().get(dir_name)
    cards_dir = (cards_dir / "cards") if cards_dir else None

    implemented = scan_implemented(cards_dir) & set(cards.all)
    exclusions = {
        n: reason for n, reason in load_exclusions().items() if n in cards.all
    }

    # --- backlog files ----------------------------------------------------
    backlog_dir = REPO_ROOT / "backlog" / "sets" / slug
    backlog_dir.mkdir(parents=True, exist_ok=True)
    cards_md = backlog_dir / "cards.md"
    mechanics_md = backlog_dir / "mechanics.md"

    if cards_md.is_file() and not args.force:
        print(
            f"skipped     {cards_md.relative_to(REPO_ROOT)} (exists; --force to overwrite)"
        )
    else:
        done, total = write_cards_md(cards_md, set_meta, cards, implemented, exclusions)
        print(
            f"wrote       {cards_md.relative_to(REPO_ROOT)} ({done} / {total} implemented)"
        )

    if mechanics_md.is_file() and not args.force:
        print(
            f"skipped     {mechanics_md.relative_to(REPO_ROOT)} (exists; --force to overwrite)"
        )
    else:
        count = write_mechanics_md(mechanics_md, set_meta, cards)
        print(
            f"wrote       {mechanics_md.relative_to(REPO_ROOT)} ({count} mechanics — DRAFT)"
        )

    worksheet = REPO_ROOT / "build" / "backlog" / f"{code}-oracle.tsv"
    write_worksheet(worksheet, cards)
    print(
        f"wrote       {worksheet.relative_to(REPO_ROOT)} (oracle worksheet, gitignored)"
    )
    print(f"cache       {dump_path(code)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
