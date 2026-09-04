# Ravnica completion work

Branch: `worktree-rav-completion`. Baseline: `4f09fec7e2`. Draft PR: #2236.

The goal is all 291 cards, their required engine functionality, and full set verification.
The initial source inventory was 239/291; the current inventory is **246/291**, with 45 missing.
The checklist is an inventory, not proof of rules correctness. Existing generated definitions
also need field and behavior review before completion can be claimed.

## Implemented in this branch

- A compositional `transmute(cost)` helper using hand activation, discard-self, sorcery timing,
  exact mana-value search, reveal, and shuffle. The helper reads the declared card mana cost
  when authored; declare `manaCost` before calling it.
- Drift of Phantasms, Dimir Infiltrator, Dizzy Spell, Muddle the Mixture, Dimir House Guard,
  Ethereal Usher, and Brainspoil, each with its own scenario file.
- Dimir Infiltrator's required Planechase 2012 printing row.
- `Effects.Regenerate(target)`, a facade over the existing regeneration effect.
- A regression fixes transmute after the discarded card is reanimated and copied in response:
  the search retains the discarded card's mana value, not the new permanent's characteristics.
- Backlog bootstrap recognizes `basicLand(...)`, matching the authoritative card inventory.

## Current verification

- `just build`: passed after all seven cards and the transmute correction (5m 37s).
- Focused scenarios: 27 passing tests across seven separate card files, including the response regression.
- Canonical-printing checks: passed for all seven cards; fresh Scryfall fields and HTTP 200 art verified.
- Snapshots: five new entries plus two corrected transmute filters in the second unit; no unrelated cards changed.
- `just assay-differential --set RAV`: no divergences among 103 compared cards. Assay declines
  transmute and does not independently verify these new abilities; all seven mtgish probes are SCAFFOLD.
- `scripts/check-card-counts.py --check`: headers in sync.
- Known baseline issue: 78 pre-existing unchecked entries across four Bloomburrow Commander
  backlog files. The user authorized continuing and disclosing this unrelated failure. Those files are untouched.
- No manual playthrough, UX pass, or end-to-end test has run yet. Existing `ActivateAbility`
  and `SelectCardsDecision` routes serve transmute; reveal events show the selected card to both players.
  `manual-scenarios/mechanics/transmute.json` is ready for manual inspection.

## Remaining work

- Implement dredge using the existing per-card draw replacement pipeline and graveyard sources.
- Finish the six remaining transmute cards: Clutch of the Undercity, Dimir Machinations,
  Grozoth, Netherborn Phalanx, Perplex, Shred Memory.
- Complete the other card-specific investigations in `mechanics.md` and all unchecked cards.
- Verify client interactions for new capabilities, and run the appropriate engine/server/client gates.
- Run the verify-set workflow over every card, printing, and token-art mapping; include self-play.
- Keep the PR in draft for review; merging is not authorized by this work plan.
