# Lorwyn completion work

This branch tracks completion of Lorwyn and the engine capabilities its cards require. The draft PR stays open for review while work proceeds; it is not ready to merge.

## Starting point

- Base: `4f09fec7e2` (merged clash support and five Lorwyn cards).
- `scripts/card-status --set LRW`: 240 / 286 unique draft cards, 46 missing.
- Twenty basic-land printings already exist; the five names count toward the 286-card checklist.
- Isolated worktree: `.claude/worktrees/lrw-completion`; branch: `codex/lrw-completion`.

## Completion requirements

- [ ] Implement every remaining card in `cards.md`, including required engine, server, and client behavior.
- [ ] Audit previously implemented cards, including the hideaway resolution and timing concerns in `mechanics.md`.
- [ ] Verify canonical printings, complete Lorwyn reprint coverage, and all basic-land arts.
- [ ] Run appropriate build, engine, scenario, serialization, and UI gates for each change.
- [ ] Review all snapshot changes and classify Assay differential disagreements.
- [ ] Run the complete `verify-set` workflow: field verification, script and token review, behavioral/self-play checks, and completion report.
- [ ] Remove the set's incomplete flag only after the completion evidence supports it; archive the backlog after verification.

## Work in progress

Five new cards are implemented: Bog Hoodlums, Nath's Elite, Fistful of Force, Spring Cleaning, and Woodland Guidance. All compose existing primitives. Canonical-printing checks, fresh Scryfall fields, image HTTP 200 checks, and snapshot validation passed. Source completeness is now 245 / 286 (41 missing). Seven focused scenario tests for the three spells passed, covering clash win/loss, target preservation, temporary bonuses, destruction scope, graveyard targeting, untapping, and self-exile.

The differential audit also found and fixed seven existing Harbinger bugs: declining the optional search incorrectly forced a search and shuffle. All seven now gate the entire search. Their 21 per-card regression tests passed. Snapshots were compared field by field: only the five new cards and seven optional gates changed from the starting point.

Assay now agrees for 108 / 111 compared canonical cards; three equivalent static-ability folds remain (see `assay-review.md`). It declines 126 cards, including all five new clash cards, and two more cards fail to fold. Those results do not verify behavior for the uncovered cards.

The earlier full builds failed only on expected new-card snapshot additions. Regeneration passed and the final `just build` for this batch passed (3m 27s, 128 tasks). No manual playthrough or end-to-end UI test has run. Champion research and required edge cases are recorded in `champion.md`; the mechanic is not implemented yet.

## Existing repository drift

The initial repository-wide backlog implementation check reported 78 implemented-but-unchecked entries in Bloomburrow Commander and its deck lists. Lorwyn had no such drift before this unit. These unrelated files are untouched. All 14 card-count headers passed the count check.
