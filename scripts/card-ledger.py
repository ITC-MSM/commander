#!/usr/bin/env python3
"""Build and verify the versioned unresolved-card work ledger.

The ledger is deliberately a *triage queue*, not a coverage claim.  It has one
row for every catalogued printing which does not currently have a matching
definition/printing in that set's Kotlin source.  New rows start as
``UNMATCHED_TRIAGE``; a reviewer must explicitly classify them in
``coverage/card-ledger-overrides.json`` before they can be scheduled.

The canonical input is the committed Scryfall-derived set-totals snapshot, so
``--check`` is offline and deterministic.  Refreshing that snapshot remains a
separate, reviewable operation (``scripts/card-status --refresh`` followed by
``scripts/gen-set-totals``).

Usage:
  scripts/card-ledger.py --write
  scripts/card-ledger.py --check
  scripts/card-ledger.py --set BLC --print
"""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import sys
from pathlib import Path

from card_exclusions import load_exclusions, load_reasons

REPO_ROOT = Path(__file__).resolve().parent.parent
SET_TOTALS = REPO_ROOT / "game-server/src/main/resources/coverage/set-totals.json"
OVERRIDES = REPO_ROOT / "coverage/card-ledger-overrides.json"
OUTPUT = REPO_ROOT / "coverage/card-implementation-ledger.json"
DEFINITIONS_ROOT = REPO_ROOT / "mtg-sets/src/main/kotlin/com/wingedsheep/mtg/sets/definitions"

SCHEMA_VERSION = 1
GENERATOR_VERSION = 1
ALLOWED_STATUSES = {
    "AUTO_CANDIDATE",
    "SCAFFOLD_REQUIRED",
    "BLOCKED_FEATURE",
    "UNMATCHED_TRIAGE",
}
SET_CODE_RE = re.compile(r'override\s+val\s+code\s*=\s*"([^"]+)"')
CARD_DSL_RE = re.compile(r'\b(?:card|basicLand)\(\s*"([^"]+)"')
PRINTING_NAME_RE = re.compile(r'\bname\s*=\s*"([^"]+)"')


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def scan_sets() -> dict[str, set[str]]:
    """Mirror card-status: scan definitions separately for each catalogue set."""
    result: dict[str, set[str]] = {}
    for directory in sorted(DEFINITIONS_ROOT.iterdir()):
        if not directory.is_dir() or directory.name == "custom":
            continue
        set_file = next(directory.glob("*Set.kt"), None)
        if set_file is None:
            continue
        match = SET_CODE_RE.search(set_file.read_text(encoding="utf-8"))
        if match is None:
            continue
        names: set[str] = set()
        cards_dir = directory / "cards"
        for card_file in cards_dir.glob("*.kt") if cards_dir.is_dir() else ():
            text = card_file.read_text(encoding="utf-8")
            names.update(CARD_DSL_RE.findall(text))
            names.update(PRINTING_NAME_RE.findall(text))
        result[match.group(1).upper()] = names
    return result


def load_overrides() -> dict[str, str]:
    if not OVERRIDES.is_file():
        raise ValueError(f"missing required override manifest: {OVERRIDES.relative_to(REPO_ROOT)}")
    payload = json.loads(OVERRIDES.read_text(encoding="utf-8"))
    if payload.get("schemaVersion") != 1:
        raise ValueError("card-ledger-overrides.json must declare schemaVersion 1")
    overrides = payload.get("overrides")
    if not isinstance(overrides, dict):
        raise ValueError("card-ledger-overrides.json overrides must be an object")
    for key, status in overrides.items():
        if not isinstance(key, str) or "|" not in key:
            raise ValueError(f"invalid ledger override key {key!r}; use SET|Card Name")
        if status not in ALLOWED_STATUSES:
            raise ValueError(f"invalid ledger override status for {key!r}: {status!r}")
    return overrides


def card_names(set_entry: dict) -> list[tuple[str, str]]:
    """Return (partition, name) without duplicating a front face across partitions."""
    draft = {entry["name"] for entry in set_entry.get("draft", [])}
    extra = {entry["name"] for entry in set_entry.get("extra", [])} - draft
    return [("draft", name) for name in sorted(draft)] + [("extra", name) for name in sorted(extra)]


def render(selected_codes: set[str] | None) -> dict:
    totals = json.loads(SET_TOTALS.read_text(encoding="utf-8"))
    exclusions = load_exclusions()
    reasons = load_reasons()
    overrides = load_overrides()
    implemented_by_set = scan_sets()
    known_override_keys: set[str] = set()
    sets = []

    for set_entry in sorted(totals, key=lambda entry: entry["code"]):
        code = set_entry["code"].upper()
        if selected_codes is not None and code not in selected_codes:
            continue
        implemented = implemented_by_set.get(code, set())
        rows = []
        for partition, name in card_names(set_entry):
            if name in implemented:
                continue
            key = f"{code}|{name}"
            known_override_keys.add(key)
            reason_key = exclusions.get(name)
            if reason_key:
                rows.append({
                    "name": name,
                    "partition": partition,
                    "status": "NOT_PLANNED",
                    "reason": {"key": reason_key, "description": reasons[reason_key]},
                })
            else:
                rows.append({
                    "name": name,
                    "partition": partition,
                    "status": overrides.get(key, "UNMATCHED_TRIAGE"),
                })
        if rows:
            sets.append({
                "code": code,
                "name": set_entry["name"],
                "releaseDate": set_entry.get("releaseDate"),
                "rows": rows,
            })

    stale_overrides = sorted(set(overrides) - known_override_keys)
    if stale_overrides:
        raise ValueError(
            "override(s) no longer refer to unresolved catalogue entries: " + ", ".join(stale_overrides)
        )
    unresolved = sum(1 for set_ in sets for row in set_["rows"] if row["status"] != "NOT_PLANNED")
    not_planned = sum(1 for set_ in sets for row in set_["rows"] if row["status"] == "NOT_PLANNED")
    return {
        "schemaVersion": SCHEMA_VERSION,
        "kind": "canonical-unresolved-card-ledger",
        "scope": {"setCodes": sorted(selected_codes) if selected_codes is not None else "all-catalogued-sets"},
        "provenance": {
            "generator": "scripts/card-ledger.py",
            "generatorVersion": GENERATOR_VERSION,
            "catalog": str(SET_TOTALS.relative_to(REPO_ROOT)).replace("\\", "/"),
            "catalogSha256": sha256(SET_TOTALS),
            "catalogDescription": "Committed Scryfall-derived set snapshot generated by scripts/gen-set-totals.",
            "implementationScanner": "Per-set Kotlin card(...) and Printing(name = ...) scan; same matching semantics as scripts/card-status.",
            "exclusions": str((REPO_ROOT / "coverage/card-exclusions.json").relative_to(REPO_ROOT)).replace("\\", "/"),
            "exclusionsSha256": sha256(REPO_ROOT / "coverage/card-exclusions.json"),
            "overrides": str(OVERRIDES.relative_to(REPO_ROOT)).replace("\\", "/"),
            "overridesSha256": sha256(OVERRIDES),
        },
        "statusDefinitions": {
            "UNMATCHED_TRIAGE": "No matching catalogue implementation and no reviewed classification. This is the safe default, not a statement about rules support.",
            "AUTO_CANDIDATE": "Reviewed candidate for a faithful implementation using existing SDK capabilities; still requires normal card review and a dedicated scenario test.",
            "SCAFFOLD_REQUIRED": "Requires human-designed card composition before implementation; it is not auto-generated or verified.",
            "BLOCKED_FEATURE": "Cannot be implemented faithfully until a named engine/SDK capability is planned and delivered.",
            "NOT_PLANNED": "Permanent documented exclusion from coverage/card-exclusions.json; it is not an implementation claim.",
        },
        "summary": {"unresolved": unresolved, "notPlanned": not_planned, "setsWithRows": len(sets)},
        "sets": sets,
    }


def serialized(ledger: dict) -> str:
    return json.dumps(ledger, ensure_ascii=False, indent=2) + "\n"


def index(ledger: dict) -> dict:
    """Small committed index for the complete (potentially multi-megabyte) ledger.

    The set/card rows remain canonical and are materialised by ``--print``.
    Committing their per-set digest keeps the tracked artifact reviewable and lets
    CI prove the full rows have not silently changed without making every normal
    card PR carry a multi-megabyte generated diff.
    """
    set_indexes = []
    for set_entry in ledger["sets"]:
        rows = set_entry["rows"]
        counts: dict[str, int] = {}
        for row in rows:
            counts[row["status"]] = counts.get(row["status"], 0) + 1
        set_indexes.append({
            "code": set_entry["code"],
            "name": set_entry["name"],
            "releaseDate": set_entry["releaseDate"],
            "rowCount": len(rows),
            "statusCounts": dict(sorted(counts.items())),
            "rowsSha256": hashlib.sha256(
                json.dumps(rows, ensure_ascii=False, separators=(",", ":")).encode("utf-8")
            ).hexdigest(),
        })
    return {
        "schemaVersion": SCHEMA_VERSION,
        "kind": "canonical-unresolved-card-ledger-index",
        "scope": ledger["scope"],
        "provenance": ledger["provenance"],
        "statusDefinitions": ledger["statusDefinitions"],
        "summary": ledger["summary"],
        "materialization": {
            "command": "scripts/card-ledger.py --print [--set CODE]",
            "description": "The command materializes canonical set/card/status rows. Each committed rowsSha256 is calculated from that set's compact JSON rows array.",
        },
        "sets": set_indexes,
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument("--write", action="store_true", help="write the committed all-set ledger")
    mode.add_argument("--check", action="store_true", help="fail if the committed all-set ledger is stale")
    mode.add_argument("--print", action="store_true", help="print a rendered ledger without writing")
    mode.add_argument("--print-index", action="store_true", help="print the compact committed ledger index")
    parser.add_argument("--set", dest="sets", action="append", help="limit --print to a set code (repeatable)")
    parser.add_argument("--slice", nargs=2, type=int, metavar=("OFFSET", "LENGTH"),
                        help="print a byte-free character slice of --print output (transport helper)")
    args = parser.parse_args()
    if (args.sets or args.slice) and not args.print:
        parser.error("--set is only valid with --print; the committed ledger always covers all catalogued sets")
    selected = {code.upper() for code in args.sets} if args.sets else None
    try:
        full_ledger = render(selected)
        content = serialized(full_ledger if args.print else index(full_ledger))
    except (ValueError, KeyError, json.JSONDecodeError) as error:
        print(f"card ledger error: {error}", file=sys.stderr)
        return 2
    if args.print:
        if args.slice:
            offset, length = args.slice
            if offset < 0 or length < 0:
                parser.error("--slice values must be non-negative")
            print(content[offset:offset + length], end="")
            return 0
        print(content, end="")
        return 0
    if args.print_index:
        print(content, end="")
        return 0
    if args.write:
        OUTPUT.write_text(content, encoding="utf-8")
        print(f"wrote {OUTPUT.relative_to(REPO_ROOT)}")
        return 0
    if not OUTPUT.is_file() or OUTPUT.read_text(encoding="utf-8") != content:
        print("card implementation ledger is stale; run `just write-card-ledger` and commit the result.", file=sys.stderr)
        return 1
    print("card implementation ledger is current")
    return 0


if __name__ == "__main__":
    sys.exit(main())
