# Unit u01 — per-turn *effect* budget for triggered abilities + 2 MSH cards

Branch `loop-msh-u01`, based on `msh-shield-counters`.

## The primitive

`TriggeredAbility.effectOncePerTurn: Boolean` — the printed rider **"Do this only once each turn"**.
An *effect* cap, deliberately distinct from the existing `oncePerTurn` *trigger* cap ("This ability
triggers only once each turn"). Per CR 603.2 / 603.2c the ability still triggers once per matching
event, so every instance goes on the stack; at most one may apply.

Where it lives:

- `mtg-sdk/.../scripting/TriggeredAbility.kt` — the flag (+ `create()` param, + a
  " Do this only once each turn." suffix on the auto-generated description).
- `mtg-sdk/.../dsl/CardBuilder.kt` — `effectOncePerTurn` in the `triggeredAbility { }` builder.
- `mtg-sdk/.../scripting/effects/GatedEffects.kt` — new `Gate.OnceEachTurn(abilityId)`. Engine-lowered
  only; cards never author it.
- `rules-engine/.../event/TriggerProcessor.kt` — `withEffectBudgetGate()` lowers the flag into
  `Gate.OnceEachTurn`, placed **inside** any enclosing consent gate (`MayDecide` / `MayPay` /
  `MayPayX`) so declining a "you may" doesn't spend the budget. Also: drops a trigger whose budget is
  already spent (rather than prompting for a decision that can't matter), and excludes capped
  abilities from the batched may-question in `batchKeyOf` (one shared yes/no would take away the
  choice of *which* instance applies).
- `rules-engine/.../handlers/effects/composite/GatedEffectExecutor.kt` — `executeOnceEachTurn()`
  checks and spends the budget atomically.
- `rules-engine/.../state/components/battlefield/BattlefieldComponents.kt` —
  `TriggeredAbilityEffectAppliedThisTurnComponent`, keyed by ability id, on the source permanent.
  Registered in `Serialization.kt`, cleared in `CleanupPhaseManager.kt`.
- `docs/card-sdk-language-reference.md` — §8 gets a trigger-cap-vs-effect-cap table plus the
  mechanics; the `Gate` catalogue gets `Gate.OnceEachTurn`.

## Cards

- **Jennifer Walters // The Sensational She-Hulk** (MSH #18, mythic) —
  `mtg-sets/.../definitions/msh/cards/JenniferWalters.kt`. Transforming DFC
  (`CardDefinition.doubleFacedCreature`, Bruce Banner shape). Front: `PlayersCantCastSpells(
  EachOpponent, IsYourTurn)` (Voice of Victory precedent) + a sorcery-speed `TransformEffect`
  activated ability. Back: reach/trample, the same lock, and the damage mirror —
  `DealsDamageEvent(recipient = CreatureYouControl)` with `TriggerBinding.ANY` (Kazarov shape),
  `MayEffect(Effects.DealDamage(ContextProperty(TRIGGER_DAMAGE_AMOUNT), Targets.Any))`,
  `effectOncePerTurn = true`.
- **Baron Strucker, HYDRA Overlord** (MSH #88, uncommon) —
  `mtg-sets/.../definitions/msh/cards/BaronStruckerHydraOverlord.kt`.
  `ModifySpellCost(YouCast(Any.withSubtype(VILLAIN)), ReduceGeneric(1))` (Tombstone / Undead Warchief
  shape) + `Triggers.entersBattlefield(Any.withSubtype(VILLAIN).youControl(), TriggerBinding.OTHER)`
  with `MayEffect(Effects.Connive(TriggeringEntity))` and `effectOncePerTurn = true`.

## Tests

- `rules-engine/.../scenarios/EffectOncePerTurnTest.kt` — the primitive, with two inline enchantments
  differing only in which cap they use: all instances trigger vs only one; declining doesn't spend the
  budget; a spent budget stops prompting; the budget resets at end of turn; two sources keep separate
  budgets; and three regression tests pinning the *existing* `oncePerTurn` behaviour unchanged.
- `rules-engine/.../scenarios/JenniferWaltersScenarioTest.kt` — multi-block (three blockers dealt
  damage in one combat): all three instances offered, only one applies; decline the early ones and
  apply a later one; a declined combat trigger leaves the budget for a bigger later hit; front-face
  lock refuses an opponent's cast on your turn.
- `rules-engine/.../scenarios/BaronStruckerScenarioTest.kt` — the second Villain still triggers after
  declining the first; only one connive per turn; his own entry doesn't trigger (OTHER binding); the
  Villain discount.

## Gate

`just test` — see the final verdict block. Snapshot reblessed with `just rebless-cards`; the diff to
`mtg-sets/src/test/resources/snapshots/cards/MSH.json` is +222 lines and contains only the two new
cards (no existing card moved). `just check-card-printing` clean for both. Backlog cards.md ticked
and resynced with `just fix-backlog` (227/276); mechanics.md's blocker section retitled SHIPPED and
its header blocked-count adjusted 47 → 45.

## Things I'm unsure about / worth a reviewer's eye

- **Which prompt is which.** For a targeted "may" trigger the engine asks the yes/no at
  *put-on-stack* time, and the prompt text is the ability's static description — so a player facing
  three She-Hulk prompts in a multi-block cannot tell which instance carries which damage number.
  That's a pre-existing engine gap (identical prompts for identical triggers), not something this
  unit introduced, but it blunts the card. Fixing it means putting triggering context into the
  decision payload.
- **Saying yes twice in one batch.** Because those may-questions are all asked before any instance
  resolves, a player can accept two and only the last-resolving one applies (the budget stops the
  rest). Correct by the rules, but a usability trap. Moving the may-question to resolution time for
  capped abilities would fix it at the cost of choosing targets for instances you then decline.
- **Dropping a spent-budget trigger.** `processSingleTrigger` returns an `AbilityFizzledEvent` and
  never puts the ability on the stack once the budget is spent. Strictly, the ability should still go
  on the stack and resolve doing nothing; the shortcut avoids a pointless prompt and mirrors the
  existing no-legal-targets shortcut, but it is an observable simplification.
- **`Gate.OnceEachTurn` is not a general Gate.** It only makes sense for the abilities the engine
  lowers into it, and it carries an `AbilityId` — slightly odd for an SDK data type. The alternative
  (a `Condition` + a marker `Effect`) needed two new types and put the correct ordering on card
  authors, which seemed worse.
- **`TriggeredAbility.description`'s new suffix is unexercised** — both shipped cards set a
  `descriptionOverride`, so the auto-generated fallback path isn't covered by a test.
