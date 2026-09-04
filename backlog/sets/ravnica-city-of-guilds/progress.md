# Ravnica completion work

Branch: `worktree-rav-completion`. Baseline: `4f09fec7e2`. Draft PR: #2236.

The goal is all 291 cards, their required engine functionality, and full set verification.
The initial source inventory was 239/291; the current inventory is **255/291**, with 36 missing.
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
- Dredge N uses the shared draw-replacement pipeline with intrinsic graveyard sources.
  The source is offered only when its owner can mill the full number; milling and returning
  compose existing effects. Numeric keyword serialization and the mtgish emitter are covered.
- Darkblast, Greater Mossdog, Grave-Shell Scarab, Moldervine Cloak, and Shambling Shell,
  each with a separate scenario file.
- Optional draw continuations preserve remaining draws through nested decisions and retain
  prior declines through pauses and saved-game reloads. A live client check exposed repeated
  offers after multiple declines; the regression now covers three dredgers and the next draw.
- Golgari Thug, Life from the Loam, Nightmare Void, and Stinkweed Imp compose existing
  primitives. Seventeen scenarios cover dredge, Thug targeting itself after death, Loam
  choosing zero through three lands and losing one target, caster-controlled discard,
  and Imp combat versus noncombat damage.
- Backlog bootstrap recognizes `basicLand(...)`, matching the authoritative card inventory.

## Current verification

- Latest four-card unit: `just build` passed (5m 30s), all 17 focused scenarios passed,
  fresh Scryfall fields/art and canonical-printing checks passed. Snapshots added exactly
  these four cards and changed no existing entry. Assay compared 103 with no divergences.

- `just test`: passed after dredge and both continuation regressions (7m 34s).
  An earlier dashboard timeout passed on focused rerun and in this final full run.
- Dredge: 14 engine scenarios, 10 scenarios across five separate card files, two SDK
  serialization tests, and two emitter tests passed. The final singular prompt wording
  was checked by rerunning the 14 engine scenarios and both browser regressions.
- Transmute: 27 scenarios across seven separate card files passed in the preceding unit;
  `just build` passed there and the full test gate above includes them again.
- Canonical-printing checks, fresh Scryfall fields, and HTTP 200 art checks passed for all
  sixteen added cards. Snapshot changes contain only the intended additions/corrections.
- `just assay-differential --set RAV`: no divergences among 103 compared cards. Assay
  declines transmute and dredge, so it does not independently verify these abilities.
- Client typecheck passed. Live client inspection and two headless Chrome regressions
  cover accepting dredge after multiple declines and declining all sources to draw normally.
  Manual scenarios are available for both transmute and dredge. Transmute has not had a
  browser playthrough yet; full-set self-play and verification remain outstanding.
- Backlog headers are in sync. Known baseline issue: 78 pre-existing unchecked entries
  across four Bloomburrow Commander backlog files. The user authorized continuing and
  disclosing this unrelated failure. Those files are untouched.

## Remaining work

- Finish the three remaining dredge cards: Golgari Brownscale (self graveyard-exit trigger),
  Golgari Grave-Troll (verify the reanimation counter count), and Necroplasm (source counters
  after it leaves before its end-step trigger resolves).
- Finish the six remaining transmute cards: Clutch of the Undercity, Dimir Machinations,
  Grozoth, Netherborn Phalanx, Perplex, Shred Memory.
- Complete the other card-specific investigations in `mechanics.md` and all unchecked cards.
- Verify client interactions for new capabilities, and run the appropriate engine/server/client gates.
- Run the verify-set workflow over every card, printing, and token-art mapping; include self-play.
- Keep the PR in draft for review; merging is not authorized by this work plan.
