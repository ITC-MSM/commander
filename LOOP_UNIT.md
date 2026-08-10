# loop-msh-u06 — Copy-with-exceptions (feature + 3 cards)

Branch: `loop-msh-u06`, off `origin/main` (`efe697ae9a`). Nothing pushed — no network in this box.

## The SDK gap

`EachPermanentBecomesCopyOfTargetEffect` exposed only `addedKeywords` / `powerOverride` /
`toughnessOverride` / `retainActivatingAbility` as copy exceptions, while its token sibling
`CreateTokenCopyOfTargetEffect` had a dozen more (name aside) and its own copy of the type-line
arithmetic. The permanent executor also honoured only `Permanent` / `EndOfTurn` / `UntilNextEndStep`,
silently degrading anything else.

## What shipped (convergence, not new riders)

- `CopyExceptions` (`mtg-sdk/.../scripting/effects/CopyExceptions.kt`) — one serializable value type
  for the whole "except …" half of a copy effect (CR 707.9b). Add/override pairs mirror CR 707.9d's
  own "in addition to its other types" distinction.
- `CopyExceptionApplier` (`rules-engine/.../handlers/effects/copy/`) — the single implementation of
  the name/type-line/keyword/P-T/color arithmetic. **Both** executors call it, and the token
  executor's Aura-host type-line probe now shares it too.
- `EachPermanentBecomesCopyOfTargetEffect.exceptions` replaces its three flat riders.
  `CreateTokenCopyOfTargetEffect` keeps its flat riders (≈20 card call sites) but exposes a
  `copyExceptions` computed view onto the same type, so its serialized shape and authoring surface
  are unchanged.
- `Duration.UntilYourNextTurn` in the copy-revert path — `RevertCopyAtYourNextTurnComponent(playerId)`,
  expired in `CleanupPhaseManager.expireUntilYourNextTurnEffects` with every other "until your next
  turn" effect.

Latent bug fixed on the way: the permanent path dropped a P/T override entirely when the copy source
had no base stats — exactly Absorbing Man copying a land.

## Cards

- **Shuri, Wakandan Inventor** [75] — `ModifySpellCost` discount + a two-target copy
  (`affected` / `target`, second wrapped in `TargetOther`) with
  `CopyExceptions(removedSupertypes = {LEGENDARY})`; without the removal the copy dies to the legend
  rule (CR 704.5j).
- **Absorbing Man** [199] — `Triggers.FirstMainPhase`, optional target across artifact / non-Aura
  enchantment / land, `Duration.UntilYourNextTurn`, and the additive exception set (name, legendary,
  creature, Human Villain, 4/4, vigilance).
- **Taskmaster, Mercenary Mimic** [232] — same shape, cross-zone target (`TargetFilter.or` over the
  graveyard) + `sourceFromAnyZone`, and the *replacing* type clause
  (`overrideCardTypes` / `overrideSubtypes`).

## Things worth a second opinion

- **Taskmaster's type clause reading.** His oracle text says "he's a legendary Human Mercenary
  Villain creature" with **no** "in addition to his other types", while Absorbing Man in the same set
  has the phrase. CR 707.9d treats that phrase as the switch, so I read Taskmaster as *replacing*
  card types and subtypes: copying an artifact creature drops the artifact type, copying a Goblin
  drops Goblin. No Scryfall rulings exist for the card yet. If a reviewer reads it the other way, the
  change is two field names in `TaskmasterMercenaryMimic.kt`.
- **Legendary is `addedSupertypes`, not an override, on Taskmaster.** `CopyExceptions` has no
  supertype-override field on purpose (supertypes only add and remove), so a copied Snow creature
  would keep Snow. Marginal, and the conservative reading.
- **Snapshot movement beyond my three cards is expected.** `Likeness Looter` (WOE) and
  `Mimeoplasm, Revered One` (DFT) are the only existing cards that used the three fields that moved
  into `exceptions`, so their serialized ability JSON changed shape. Nothing else moves.

## Gate

See the PR body (`build/pr/loop-msh-u06-body.md`) for the exact commands and results.
