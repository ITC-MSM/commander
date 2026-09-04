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

Bog Hoodlums and Nath's Elite are the first implementation unit. Both compose existing blocking abilities and the clash pattern. Canonical-printing checks pass. Assay declines the clash lines; its result is not behavioral verification. The required `just build` gate is running; snapshots, final validation, and commits remain pending.

## Existing repository drift

The initial repository-wide backlog implementation check reported 78 implemented-but-unchecked entries in Bloomburrow Commander and its deck lists. Lorwyn had no such drift before this unit. These unrelated files are untouched. All 14 card-count headers passed the count check.
