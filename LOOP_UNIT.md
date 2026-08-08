# Unit u01 — per-turn *effect* budget for triggered abilities + 2 MSH cards

Branch `loop-msh-u01`, based on `msh-shield-counters`.

## The primitive

`TriggeredAbility.effectOncePerTurn: Boolean` — the printed rider **"Do this only once each turn"**,
deliberately distinct from the existing `oncePerTurn` cap ("This ability triggers only once each
turn"). **CR 603.2h:** *"A triggered ability may have an instruction followed by 'Do this only once
each turn.' This ability triggers only if its source's controller has not yet taken the indicated
action that turn."* So while the action is untaken every matching event triggers its own instance;
once it is taken the ability stops triggering for the rest of the turn and instances already on the
stack do nothing as they resolve (Nykthos Paragon / Riveteers Ascendancy rulings). The contrast with
`oncePerTurn` is what spends the cap: the *first trigger* there, the *action* here.

Where it lives:

- `mtg-sdk/.../scripting/TriggeredAbility.kt` — the flag (+ `create()` param, + a
  " Do this only once each turn." suffix on the auto-generated description).
- `mtg-sdk/.../dsl/CardBuilder.kt` — `effectOncePerTurn` in the `triggeredAbility { }` builder.
- `mtg-sdk/.../scripting/effects/GatedEffects.kt` — new `Gate.OnceEachTurn(abilityId)`. Engine-lowered
  only; cards never author it.
- `rules-engine/.../event/TriggerProcessor.kt` — `withEffectBudgetGate()` lowers the flag into a
  sandwich of `Gate.OnceEachTurn` gates: the spending one **inside** any enclosing consent gate
  (`MayDecide` / `MayPay` / `MayPayX`) so declining a "you may" costs nothing, and a `spend = false`
  check **outside** it so an instance whose turn is already used up resolves silently. Also: drops a
  matching event once the action has been taken (CR 603.2h — it never triggers), and excludes capped
  abilities from the batched may-question in `batchKeyOf` (one shared yes/no would take away the
  choice of *which* instance to use).
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
  differing only in which cap they use: declining down a batch keeps offering the next instance;
  taking the action makes the rest resolve silently (no prompt); a later matching event that turn
  doesn't trigger; the budget resets at end of turn; two sources keep separate budgets; and three
  regression tests pinning the *existing* `oncePerTurn` behaviour unchanged.
- `mtg-sdk/.../scripting/TriggeredAbilityDescriptionTest.kt` — the auto-generated
  " Do this only once each turn." suffix (and that `oncePerTurn` doesn't get it).
- `rules-engine/.../scenarios/JenniferWaltersScenarioTest.kt` — multi-block with three blockers dealt
  **1 / 2 / 5** in one combat-damage event: all three instances offered when declined, only one
  mirror lands; accepting the first silences the rest; taking a different instance in each of three
  runs yields all three numbers, which is what pins `TRIGGER_DAMAGE_AMOUNT` per instance and proves
  "decline down to the 5" is reachable. Plus: a declined combat trigger leaves the turn's use for a
  bigger later hit; front-face lock refuses an opponent's cast on your turn.
- `rules-engine/.../scenarios/BaronStruckerScenarioTest.kt` — the second Villain still triggers after
  declining the first; only one connive per turn; his own entry doesn't trigger (OTHER binding); the
  Villain discount.

## Gate

Re-gated after the review corrections: `just test` — BUILD SUCCESSFUL, 10,994 tests passed, none
failed. (The first attempt died with "Gradle build daemon disappeared" during `:mtg-sets:test` — the
box's known OOM kill, no test failure; re-run after reaping the orphaned workers.) `just
rebless-cards` re-run: **no snapshot file changed**, the corrections don't touch card definitions.

Original gate:

`just test` — see the final verdict block. Snapshot reblessed with `just rebless-cards`; the diff to
`mtg-sets/src/test/resources/snapshots/cards/MSH.json` is +222 lines and contains only the two new
cards (no existing card moved). `just check-card-printing` clean for both. Backlog cards.md ticked
and resynced with `just fix-backlog` (227/276); mechanics.md's blocker section retitled SHIPPED and
its header blocked-count adjusted 47 → 45.

## Things I'm unsure about / worth a reviewer's eye

*(Revised after review — two of the original entries were wrong and are corrected here.)*

- **Which prompt is which.** The prompt text is the ability's static description, so a player facing
  three She-Hulk prompts in a multi-block cannot tell which instance carries which damage number.
  Pre-existing engine gap (identical prompts for identical triggers), not introduced here, but it
  blunts the card. Fixing it means putting triggering context into the decision payload. Still open.
- **~~Saying yes twice in one batch.~~ Fixed in review correction.** The may-questions used to all be
  asked at put-on-stack time, so a player could accept several and get one application. The lowering
  now puts a `spend = false` check *outside* the consent gate, which moves consent for a capped
  targeted trigger to resolution time — where CR puts it anyway (targets on announcement, CR 603.3d;
  the "you may" as the ability resolves, per the Legolas ruling). An instance whose turn is already
  used up now resolves silently.
- **Dropping a matching event once the action is taken is the rule, not a shortcut.** CR 603.2h: the
  ability "triggers only if its source's controller has not yet taken the indicated action that
  turn", and the Riveteers Ascendancy ruling is explicit — *"Once you have chosen to return a creature
  to the battlefield, further instances of sacrificing creatures the same turn will not cause the
  ability to trigger."* Putting it on the stack anyway would be the bug. It is dropped with **no**
  event (an `AbilityFizzledEvent` would be a phantom ability in the log: nothing triggered).
- **Which instance spends the budget: the *first to resolve*.** The stack is LIFO, so that is the
  *last* instance put on the stack, not the last to resolve. (The original note had this backwards.)
- **`Gate.OnceEachTurn` is not a general Gate.** It only makes sense for the abilities the engine
  lowers into it, and it carries an `AbilityId` — slightly odd for an SDK data type. `EffectContext`
  already carries `abilityIdentity`, but that is definition-scoped and null for synthesized sources,
  which would silently merge or lose budgets; the explicit id is the safer choice. The alternative
  (a `Condition` + a marker `Effect`) needed two new types and put the correct ordering on card
  authors, which seemed worse.
- **The consent gate must be outermost.** `withEffectBudgetGate` recognises `May*` only at the top of
  the effect tree; a "you may" buried under a `CompositeEffect` would get the budget gate outside it
  and spend the turn's use on a decline. Neither shipped card hits it; documented in the flag's KDoc
  and in the SDK reference.
