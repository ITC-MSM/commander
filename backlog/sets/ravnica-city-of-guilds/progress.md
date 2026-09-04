# Ravnica completion work

Branch: `worktree-rav-completion`. Baseline: `4f09fec7e2`.

The goal is all 291 cards, their required engine functionality, and full set verification.
The initial source inventory reports 239 implemented cards and 52 missing. The checklist
is an inventory, not proof of rules correctness. Existing generated definitions also need
field and behavior review before completion can be claimed.

## Work remaining

- Complete the card-specific investigations recorded in the mechanics inventory.
- Implement the 52 unchecked cards, including dredge, transmute, and other required capabilities.
- Exercise new mechanics through engine tests and the client decision flows.
- Run the card, engine, snapshot, canonical-printing, and Assay differential gates.
- Run the verify-set workflow over the complete corpus, token art, and printings.
- Keep this PR in draft for review; no merge is authorized by this work plan.

## Verification and known baseline issues

- Baseline `scripts/card-status --set RAV`: 239 / 291, 52 missing. Current source inventory: 241 / 291, 50 missing.
- `scripts/check-card-counts.py --check`: all 14 headers in sync.
- `scripts/check-backlog-implementations.py --check`: 78 pre-existing unchecked entries
  across four Bloomburrow Commander backlog files. The user authorized continuing Ravnica
  work and disclosing this unrelated failure. Those files are untouched.
- The backlog bootstrap omitted `basicLand(...)` declarations; its scanner now recognizes
  them, matching `scripts/card-status`. All five Ravnica basic land names already exist.
- `just build` passes (9m 46s). No manual playthrough, UX pass, or end-to-end test has run for this branch yet.

## Current implementation unit: transmute

- `transmute(cost)` composes existing hand activation, sorcery timing, discard-self cost,
  exact mana-value search, reveal, and shuffle. No new executor or decision type is introduced.
- Drift of Phantasms and Dimir Infiltrator are written with individual scenario files.
- All 14 scenarios in the two-card matrix pass, including reveal and shuffle events, failed searches, insufficient mana, and timing/zone restrictions.
- Dimir Infiltrator also needs a Planechase 2012 printing row; that row is written.
- Assay declines Drift’s transmute line; the mtgish probe reports SCAFFOLD for Transmute.
  Neither analyzer supplies behavioral proof of this new composition.
- Client routing is existing `ActivateAbility` pipeline plus `SelectCardsDecision` picker,
  with card information supplied by the server and reveal events sent to both players.
- `manual-scenarios/mechanics/transmute.json` provides both hand activations and normal
  creature casting for manual inspection; it has not been played through yet.
- Focused combined test job: `/tmp/rav-transmute-both.log`. Snapshot job: `/tmp/rav-rebless.log`.
  Builds may queue behind the machine-wide semaphore; do not restart a live job.

Transmute unit gate: `just build` passed; both canonical-printing checks passed after the
PC2 row was added. `just assay-differential --set RAV` reported no divergences, but both
transmute cards decline and are not covered by that comparison. Snapshot comparison
confirmed exactly two new entries and zero changes to existing cards. Before closing
out the mechanic, extend the response-interaction tests to cover the discarded card
changing zones/characteristics while the ability is on the stack.
