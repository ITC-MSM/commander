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

Bog Hoodlums and Nath's Elite are implemented with existing blocking abilities and the clash pattern. Canonical printing, fresh Scryfall fields and image URLs, and snapshot checks passed. Source completeness is now 242 / 286 (44 missing). Assay declines their clash text; this is not behavioral verification.

The differential audit found seven existing Harbinger bugs: declining the optional search incorrectly forced a search and shuffle. All seven now gate the entire search. Their 21 per-card regression tests passed, covering declining, finding a card, and accepting but finding no card. Snapshot changes were checked field by field: only the two new cards and seven optional gates changed. The differential now agrees for 108 / 111 compared cards; three equivalent static-ability folds remain (see `assay-review.md`).

The first `just build` failed only on the expected new-card snapshot additions. The initial implementation batch is recorded in the draft PR with its focused checks passing. The broader build remains pending: its queued invocation was stopped before Gradle started so newly authored cards can be snapshotted first. Fistful of Force, Spring Cleaning, and Woodland Guidance are now authored locally with seven focused tests; their fields, art URLs, and canonical-printing checks pass, and their snapshot job is queued. They are not yet checked off in this committed checklist. No manual playthrough or end-to-end UI test has run.

## Existing repository drift

The initial repository-wide backlog implementation check reported 78 implemented-but-unchecked entries in Bloomburrow Commander and its deck lists. Lorwyn had no such drift before this unit. These unrelated files are untouched. All 14 card-count headers passed the count check.
