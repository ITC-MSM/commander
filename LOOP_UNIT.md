# HOB u01 implementation handoff

## Scope shipped

- Inventory authority: Scryfall HOB cache refreshed on 2026-08-04 during previews.
- Revealed inventory: 193 cards (the set releases 2026-08-14, so this is not a claim about unrevealed cards).
- Refreshed upstream mtgish from the 2026-08-04 Scryfall Oracle bulk data and regenerated its JSONL locally.
- Vendored HOB as an emitter regression fixture: 193 IR/Scryfall inputs plus deterministic emitted golden.
- Implemented the 47 cards classified true `AUTOGEN`: 41 new nonbasic definitions, the five HOB basic-land printings, and one `Wood Elves` reprint row.
- Added `TheHobbitSet` and a dated 193-card backlog inventory.

## Deliberately dropped

- 71 `SCAFFOLD` cards were not shipped. They are bridge-coverable but their emitted drafts contain explicit missing structure (multi-face, target recovery, conditionals, token shapes, and trigger shapes), so they are not automatic implementations.
- 75 `BLOCKED` cards were not shipped because they require capability work, led by amass, recruit, storied, Saga chapters, draw-N and attack triggers.
- No generated draft containing `TODO` or `STRUCTURE needs human wiring` remains under the HOB cards package.

## Verification

- `just coverage-fixtures HOB` — passed; vendored 193-card fixture and golden.
- `just test` — passed after the expected HOB snapshot was blessed.
- `just rebless-cards` — passed; only the new `HOB.json` card snapshot was created.
- `git diff --check` — passed.
- `just check-card-printing` — passed for all 41 new nonbasic HOB canonicals.
- `Wood Elves` is correctly canonical in POR and the HOB reprint row is present, but its printing check fails because the repository already lacks an unrelated KHC reprint row.
- The five basic-land names were not counted as passing printing checks: the repository's printing checker reports pre-existing global basic-land canonical/reprint drift across many sets. HOB's five set-specific `basicLand` entries are covered by the passing catalog, discovery, art-order, snapshot, and round-trip tests.

## Review notes / uncertainties

- HOB is still previewing. Re-run the Scryfall inventory and regenerate the HOB fixture after additional reveals.
- Generated cards use existing SDK primitives only; no engine/SDK vocabulary was introduced.
- The 71 scaffold cards need separate human implementation work and must not be promoted from their generated stubs unchanged.
