# Card implementation ledger

`coverage/card-implementation-ledger.json` is the committed, deterministic index of catalogue rows that are not found by the same per-set Kotlin scan used by `scripts/card-status`. It is a planning input, never evidence that a card is playable, tested, or rules-correct. Its per-set `rowsSha256` values make the full set/card/status queues tamper-evident without forcing every card PR to carry a multi-megabyte generated diff.

The ledger is generated from the committed Scryfall-derived `set-totals.json`, the card-exclusion policy, and reviewed overrides. Its provenance includes SHA-256 digests for all three inputs. `scripts/card-ledger.py --print --set BLC` materializes the canonical set/card/status entries represented by BLC's committed digest. It therefore stays offline and reviewable: a Scryfall refresh is a separate denominator change, first made through `scripts/card-status --refresh` and `scripts/gen-set-totals`.

## Statuses

- `UNMATCHED_TRIAGE`: default for an unresolved catalogue row. It has not yet been assessed.
- `AUTO_CANDIDATE`, `SCAFFOLD_REQUIRED`, `BLOCKED_FEATURE`: reviewed planning labels, set only in `coverage/card-ledger-overrides.json`.
- `NOT_PLANNED`: a permanent named exclusion from `coverage/card-exclusions.json`; it is never a claim of implementation.

An implemented card is intentionally absent. It is not automatically called `IMPLEMENTED_VERIFIED`: verification requires the dedicated scenario test and evidence defined in `docs/plans/commanderonline-card-program.md`.

## Workflow and CI gate

Run `just check-card-ledger` in every card or catalogue PR. It is a fast, network-free consistency gate. If the source scan, exclusions, or approved overrides changed, run `just write-card-ledger`, review the exact queue delta, and commit it together with the cause. CI runs the same check.

Do not add a classification merely to improve a percentage. A `BLOCKED_FEATURE` entry must name its capability in the corresponding planning issue/backlog; an `AUTO_CANDIDATE` still goes through the `add-card` workflow, canonical-printing check, one-card/one-test rule, and independent review.
