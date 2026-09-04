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

Seven new cards are implemented: Bog Hoodlums, Nath's Elite, Fistful of Force, Spring Cleaning, Woodland Guidance, Sentry Oak, and Springjack Knight. All compose existing primitives. Canonical-printing checks, fresh Scryfall fields, image HTTP 200 checks, and snapshot validation passed. Source completeness is now 247 / 286 (39 missing). Seven focused scenario tests for the three spells passed, covering clash win/loss, target preservation, temporary bonuses, destruction scope, graveyard targeting, untapping, and self-exile.

The differential audit also found and fixed seven existing Harbinger bugs: declining the optional search incorrectly forced a search and shuffle. All seven now gate the entire search. Their 21 per-card regression tests passed. At that stage, snapshots were compared field by field: only the five new cards and seven optional gates changed from the starting point.

The earlier five-card batch's Assay audit agrees for 108 / 111 compared canonical cards; three equivalent static-ability folds remain (see `assay-review.md`). It declines 126 cards, including all five new clash cards, and two more cards fail to fold. Those results do not verify behavior for the uncovered cards.

The earlier full builds failed only on expected new-card snapshot additions. Regeneration passed and the final `just build` for this batch passed (3m 27s, 128 tasks). No manual playthrough or end-to-end UI test has run. Champion research and required edge cases are recorded in `champion.md`; the mechanic is not implemented yet.

The linked-exile source-visit prerequisite is now implemented. An old leaves trigger reads its
original source visit's pile after a blink or token cleanup; leaving exile invalidates previous
links. Seven engine scenarios passed, including a full game-state serialization round trip.
`just test-rules` passed (2m 19s, 61 tasks). This internal state change reuses existing events and
selection UI; no client interaction was added. Champion itself remains unimplemented: distinct
linked ability pairs, champion events, and the remaining behavioral matrix are tracked
in `champion.md`.

The next source-reference prerequisite is verified: old abilities do not act on a source that has
already returned, while effects can still return and then modify their own source. Source-relative
filters distinguish battlefield visits too. Sentry Oak and Springjack Knight have eight individual
card scenarios covering clash results, timing, decline, target loss, duration, and absent/returned
sources. Their printing checks and fresh Scryfall/image rechecks passed. Snapshot comparison found
only these two additions and no changes to existing entries. `just test` passed for the combined
batch (4m 27s, 106 tasks), including engine, card, SDK, AI, gym and server suites. Both new cards'
clash lines are declined by Assay; the generator supplies scaffolds rather than complete scripts.

## Existing repository drift

The initial repository-wide backlog implementation check reported 78 implemented-but-unchecked entries in Bloomburrow Commander and its deck lists. Lorwyn had no such drift before this unit. These unrelated files are untouched. All 14 card-count headers passed the count check.
