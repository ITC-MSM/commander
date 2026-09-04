# Ravnica completion work

Branch: `worktree-rav-completion`. Baseline: `4f09fec7e2`.

The goal is all 291 cards, their required engine functionality, and full set verification.
The initial source inventory reports 239 implemented cards and 52 missing. The checklist
is an inventory, not proof of rules correctness. Existing generated definitions also need
field and behavior review before completion can be claimed.

## Work remaining

- Finish the mechanics inventory (the generated mechanics document is explicitly a draft).
- Implement the 52 unchecked cards, including dredge, transmute, and other required capabilities.
- Exercise new mechanics through engine tests and the client decision flows.
- Run the card, engine, snapshot, canonical-printing, and Assay differential gates.
- Run the verify-set workflow over the complete corpus, token art, and printings.
- Keep this PR in draft for review; no merge is authorized by this work plan.

## Verification and known baseline issues

- `scripts/card-status --set RAV`: 239 / 291, 52 missing.
- `scripts/check-card-counts.py --check`: all 14 headers in sync.
- `scripts/check-backlog-implementations.py --check`: 78 pre-existing unchecked entries
  across four Bloomburrow Commander backlog files. The user authorized continuing Ravnica
  work and disclosing this unrelated failure. Those files are untouched.
- The backlog bootstrap omitted `basicLand(...)` declarations; its scanner now recognizes
  them, matching `scripts/card-status`. All five Ravnica basic land names already exist.
- No engine build, manual playthrough, UX pass, or end-to-end test has run for this branch yet.
