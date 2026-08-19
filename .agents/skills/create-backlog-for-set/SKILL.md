---
name: create-backlog-for-set
description: Create the `backlog/sets/<set-name>/` entry for a Magic set — download the whole set from Scryfall (cached alongside Assay's cache), write `cards.md` as a per-colour checklist with the already-implemented cards ticked, scaffold the set's `definitions/<code>/` module if it doesn't exist, and write `mechanics.md` ordering every mechanic the set uses with a checkbox, a description, and its card list. Use when asked to "create a backlog for set X", "add set X to the backlog", "start work on set X", "scaffold set X", or when a set has no `backlog/sets/` entry yet.
argument-hint: <SET code or name>
---

# Create a set's backlog

Turn a set name or code into the three things the rest of the tooling needs: a scaffolded set
module, `backlog/sets/<set-name>/cards.md` (the checklist `add-card`, `add-random-card`, `set-loop`
and `just check-backlog` all read), and `backlog/sets/<slug>/mechanics.md` (the mechanic-by-mechanic
map that says what `add-feature` work the set implies).

The mechanical half is [`bootstrap-set.py`](bootstrap-set.py) — set resolution, the Scryfall
download, the draft/extras split, colour grouping, ticking what's already implemented, counting.
The judgement half is yours: what each mechanic *means*, whether the engine already models it, and
the set themes Scryfall has no keyword for. **Never hand-roll the mechanical half** — the counts
have to match `scripts/card-status` exactly or `just check-backlog` goes red.

## Step 1 — run the bootstrap

```bash
python3 .agents/skills/create-backlog-for-set/bootstrap-set.py "<code or name>"
```

Accepts either form: `DRK` or `"The Dark"`. An ambiguous name exits with the matching codes — pick
one and re-run. Useful flags:

| Flag | When |
|------|------|
| `--slug <name>` | the backlog directory should not be the kebab-cased set name |
| `--no-scaffold` | backlog files only; don't touch `mtg-sets/` |
| `--refresh` | re-download the dump (a set still in spoiler season, or a suspect cache) |
| `--force` | overwrite an existing `cards.md` / `mechanics.md` |
| `--object-name` | the derived Kotlin object name is wrong or invalid — the repo shortens long titles (`LostCavernsOfIxalanSet`, `DuskmournSet`), so a set with a subtitle usually wants this |

It prints one line per artifact. What it writes:

- `~/.cache/scryfall/_setdump-<code>.json` — every printing in the set, `unique=prints`. Cached in
  the same directory `scripts/card-status`, `:mtgish-tooling` and Argentum Assay use, under a
  `_setdump-` prefix that can't collide with a set code. Released sets are frozen, so a second run
  costs no network. Not committed.
- `mtg-sets/<era>/…/definitions/<code>/<Name>Set.kt` + an empty `cards/`, if the set had no module.
  The era comes from the release year; a year past the newest era module is an error, not a guess
  (a new era needs `settings.gradle.kts`, which is `add-feature` work).
- `backlog/sets/<slug>/cards.md` — the checklist.
- `backlog/sets/<slug>/mechanics.md` — a **draft**, marked as one. Step 3 is what finishes it.
- `build/backlog/<code>-oracle.tsv` — one row per card (name, rarity, cost, type, keywords, reprint
  flag, oracle text on one line). Gitignored; it exists so Step 3 can read a whole set's text
  cheaply.

**`--force` overwrites a hand-maintained file.** Without it the script skips files that already
exist, which is the right default when re-running against a set someone has been curating. Only
force when you mean to discard what's there.

## Step 2 — finish the scaffold (only if it created one)

The generated `<Name>Set.kt` carries `code`, `displayName`, `releaseDate` and `incomplete = true`.
Three fields need a human decision, so the script leaves them off:

- **`block`** — the block the set belongs to, if any (`override val block = "Shadowmoor"`).
- **`basicLandsFallback`** — only when the set prints no basics of its own. Point it at the set
  whose lands should be registered alongside it: its block's base set, else `PortalSet`.
  Check the dump: `grep -o '"type_line":"Basic Land[^"]*"' ~/.cache/scryfall/_setdump-<code>.json | sort -u`.
- **`sealedSupported = false`** — for sets that can't carry a limited environment at all
  (Commander/duel decks, planar sets, promo sets). A merely unfinished set doesn't need it;
  `incomplete = true` already keeps it out of the picker's default list.

Confirm the set is discoverable before moving on — `scripts/card-status --set <CODE>` must report
it (it reads `code` out of the Kotlin source, so a set that doesn't show up is a scaffold problem).
Gate with `scripts/gradle-locked :mtg-sets:<era>:compileKotlin` — a scaffold-only change reaches nothing
else, so a full `just test` is wasted time (see CLAUDE.local.md's gate table).

> **Do not run `scripts/gen-set-totals` to "register" the new set.** It rewrites the committed
> 6 MB `coverage/set-totals.json` for *every* set from the local Scryfall cache, so running it with
> a partly-populated cache silently empties other sets' totals. It's a separate, full-cache chore.

## Step 3 — write `mechanics.md` properly

The draft lists what Scryfall names: `keywords`, plus any `Word —` ability-word headers found in
oracle text, each ordered by card count with its card list. That's the skeleton. Three passes turn
it into the document:

**a. Read the set.** `build/backlog/<code>-oracle.tsv` is the whole set's text in one file. For a
set up to ~150 cards read it directly. For a bigger one, dispatch subagents over batches of 30–40
rows — "return only: recurring templates you saw, each as `NAME | one-line description | card
names`" — and merge. Same doctrine as `verify-set`: they read, you hold the verdicts.

**b. Prune and fill the drafted entries.** For each one, write the description (what it does, and
the CR rule number that defines it — verify the number in the local `MagicCompRules_*.txt`, never
from memory) and replace `**Engine support:** _unverified_` with a real verdict:

- **Supported** → tick the box and name the primitive that models it, e.g.
  **Engine support:** ✅ `Keyword.TRAMPLE` — checked against
  [`docs/card-sdk-language-reference.md`](../../../docs/card-sdk-language-reference.md) and the SDK
  `Keyword` enum, not against your recollection.
- **Not supported** → leave the box unticked and say in one line what's missing. That entry is now
  an `add-feature` unit, and the cards under it are blocked.

Ability-word rows are noisy by construction — level-up rows and flavour lines match the same shape.
Delete the ones that aren't mechanics.

**c. Add the set themes.** The mechanics that carry a set are often the ones with no keyword: an
artifact-matters axis, sacrifice-as-cost fuel, a tribal payoff, an unusual timing restriction. Add
one section per theme in the same shape (checkbox, description, card list), and order every section
— named and unnamed together — by card count, most cards first. `backlog/sets/antiquities/MECHANICS.md`
is the reference for what a good one reads like on a set with no named keywords at all.

Keep the card lists complete. Their job is to answer "if I build this, what does it unlock?" — a
truncated list makes the whole file unusable for backlog triage.

## Step 4 — verify and commit

```bash
scripts/check-card-counts.py --check              # cards.md headers match the checkboxes
scripts/check-backlog-implementations.py --check  # no [ ] entry is actually implemented
scripts/card-status --set <CODE>                  # the authoritative Done / Total
```

The first two also report **pre-existing** drift in other sets' backlogs — read the paths before
reacting, and don't fix someone else's file (AGENTS.md → "Focus on your own work"). `card-status`'s
Done/Total must equal `cards.md`'s `**Implemented:**` line; if it doesn't, run
`scripts/check-card-counts.py --fix` and find out why they disagreed before committing.

Commit `backlog/sets/<slug>/` and any scaffolded `<Name>Set.kt`. Do **not** commit the Scryfall
dump or the TSV worksheet — the dump lives in the cache and `build/` is gitignored.

## Format rules that other tooling depends on

Change these and the repo's checkers stop working:

- The `cards.md` H1 is exactly `# <Set Name> (<CODE>) - Card Checklist` — an ASCII hyphen, and the
  code in parentheses. `scripts/check-backlog-implementations.py` finds the set by that regex.
- A card line is `- [x] Name` / `- [ ] Name` and **nothing after the name**. No annotations, no
  `(reprint)` suffixes: the name is matched verbatim against `card("Name")` and `name = "Name"` in
  the Kotlin sources, so a suffix makes the entry invisible to the checker and to `--fix`.
- DFC and Adventure cards use the full `Front // Back` name; only the front face is matched.
- `**Implemented:** N / M` on its own line, N = `[x]` count, M = `[x]` + `[ ]`. `[-]` (not planned,
  from `coverage/card-exclusions.json`) is deliberately outside both counts.
- Cards a set prints but boosters don't contain go under `### Extras` — that's the same draft /
  extra split `scripts/card-status` reports, and mixing them corrupts the denominator.
