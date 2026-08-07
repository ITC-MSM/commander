# Marvel Super Heroes (MSH) — Missing Mechanics

Cards from MSH that **cannot** be implemented with the current engine/SDK, grouped by the
missing mechanic they need. Each mechanic is `add-feature` territory (a new SDK primitive,
keyword, or engine capability) — not pure card authoring.

Scope: the 276 booster cards (collector numbers 1–276). Triaged against the SDK on 2026-08-04,
updated 2026-08-07 after **power-up shipped**. **47 of the 276 are blocked**; every other card is
buildable from existing primitives.

Supported today and *not* a blocker despite looking like one: **power-up** (see the first section
below — the keyword, its once-only limit and its pip-wise cost reduction all ship), **harness / ∞ abilities**
(`CounterType.HARNESS` already ships, backing the SPM Infinity Stones — see
`definitions/spm/cards/TheSoulStone.kt`), the **Hero** and **Villain** creature types
(`Subtype.HERO`, `Subtype.VILLAIN`), the **Plan** enchantment *subtype* (`Subtype` is a free-form
`@JvmInline value class`, so no registration is needed), **sneak**, **connive**, **saga**, **crew**,
**landcycling**, **finality/stun counters**, and **registering a new passive named counter** (that is
documented routine card work — see `docs/card-sdk-language-reference.md` §16).

---

## Power-up — SHIPPED ✅ (20 of 24 cards unblocked)

> Power-up — {4}{W}: Put two +1/+1 counters on this creature. *(Activate each power-up ability
> only once. Reduce the cost by its mana cost if it entered this turn.)*

The set's marquee mechanic, implemented 2026-08-07 against **CR 702.193** (the rules text ships the
keyword; don't work from the reminder text alone). Authoring is `isPowerUp = true` in an
`activatedAbility { }` block — the same shape as `isExhaust`, deliberately, since both mean
"activate only once". See `docs/card-sdk-language-reference.md` → *Power-up*.

What that flag does:

1. **"Activate only once"** — the DSL auto-adds `ActivationRestriction.Once`, a per-object lifetime
   limit. Per CR 400.7 a re-entering permanent is a new object and may power up again; the existing
   `AbilityActivatedEverComponent` tracker gives that for free.
2. **The prefix** — `ActivatedAbility.describeWithCost` renders `"Power-up — "`, and the enumerator
   rebuilds the label from the *effective* cost, so the menu shows the discounted cost on the turn
   the permanent entered.
3. **The cost reduction** — `ManaCost.subtract(other)` in `mtg-sdk/.../core/ManaCost.kt` implements
   the pip-wise reduction of CR 702.193b / 118.7 (generic reduces generic; colored and colorless
   reduce their own type with excess spilling to generic; hybrids cancel identical hybrids then
   either half; `{X}` inert on both sides). `CastPermissionUtils.applyPowerUpSelfReduction` applies
   it gated on `EnteredThisTurnComponent`, from inside `applyActivatedAbilityCostReduction` — so all
   three read sites (the enumerator and both `ActivateAbilityHandler` sites) stay in lockstep.
   `ManaCost.subtract` is also exactly what **offering** (CR 702.48c) needs, if that ever comes up.

`ReduceActivatedAbilityCost.powerUpOnly` shipped alongside, mirroring `exhaustOnly`, which unblocks
**Hulk, Gamma Goliath** [215] ("Power-up abilities of other creatures you control cost {3} less").

Tests: `ManaCostSubtractTest` (every printed MSH power-up cost/mana-cost pair, plus the CR 118.7
subrules the set doesn't exercise) and `PowerUpKeywordScenarioTest` (once-only, re-entry reset,
entered-this-turn gating, displayed-vs-paid lockstep, stacking with `powerUpOnly`).

**Now buildable as ordinary card work (20):** Brave Brawler [8] · Captain Marvel, Earth's Protector
[11] · Aerial Doombot [43] · Bold Biochemist [48] · Stature,
Size Shifter [76] · Ninja of the Hand [108] · Unliving Legionnaire [119] · Human Torch, Johnny Storm
[136] · Quicksilver, Brash Blur [148] · Volcanic Villain [159] · Hercules, Prince of Power [171] ·
Pet Avengers [178] · Serpent Specialist [186] · She-Hulk, Jade Defender [188] · White Tiger, Ava
Ayala [196] · Abomination, Terrifying Titan [198] · Hulk, Gamma Goliath [215] · Thanos, the Mad Titan
[233] · Ultron Drone [253] · Viv Vision, Teen Synthezoid [256].

Non-blocking notes for those: Stature's "can't be blocked if her power is 1 or less" is a
`ConditionalStaticAbility`; Quicksilver's opening-hand clause is `mayBeginGameOnBattlefield()`;
Thanos's odd/even sweep is `.manaValueIsOdd()` / `.manaValueIsEven()` + a modal.

### Still blocked — 4 cards, each needing one more thing ⛔

- **Wonder Man, Hollywood Hero** [160] — "Each power-up ability of permanents you control can be
  activated an additional time" must *raise* the limit. `ActivationRestriction.Once` is a fixed
  `data object`; needs either `ActivationRestriction.MaxPerGame(count: DynamicAmount)` or a
  `GrantExtraPowerUpActivations(filter, amount)` static consulted where `Once` / `MaxPerTurn` are
  enforced. `IgnoreExhaustActivationLimit` / `ExhaustActivationWaiver` is the structural precedent,
  but it *waives* the limit rather than raising it by one.
- **Kang the Conqueror** [62] — "During that turn, power-up abilities can't be activated" needs a
  turn-scoped flag on `GameState`, read where granted `PreventActivatedAbilities` is read
  (`CastPermissionUtils.isActivationPrevented`) and gated on `ActivatedAbility.isPowerUp`, which now
  exists. The extra turn itself is fine (`Effects.TakeExtraTurn`).
- **Nick Fury, Agent of S.H.I.E.L.D.** [25] — the power-up and the top-seven dig are both ordinary
  composition (Gather → Select → Move, Gishath's shape), but *"If it's a double-faced card, you may
  transform it"* has no faithful modelling: there is no "is a double-faced card" predicate anywhere
  in the SDK, so the optional transform can only be offered unconditionally — a prompt on a
  single-faced permanent, where the printed card offers none. Needed: a `StatePredicate.IsDoubleFaced`
  (the card component already knows its back face; it is the *predicate* and its `PredicateEvaluator`
  branch that are missing), then wrap the existing `MayEffect(ForEachInCollectionEffect(…,
  TransformEffect))` in a `ConditionalEffect` over it. Note the transform must stay *post-entry* —
  the printed order puts the card onto the battlefield first, so its ETB triggers fire on the front
  face and only then does it flip, which is not the same as entering transformed.
- **Loki Laufeyson** [143] — the power-up half is done; the *other* ability needs a delayed "when you
  next cast" trigger whose spell filter is source-relative
  (`CardPredicate.ManaValueAtMostDynamic(DynamicAmounts.sourcePower())` — the predicate exists, but
  nothing evaluates a source-relative dynamic filter inside delayed-trigger matching).

## Teamwork N — 13 cards ⛔

> Teamwork 4 *(As an additional cost to cast this spell, you may tap any number of creatures you
> control with total power 4 or more.)*

The set's second new mechanic: an **optional** additional cast cost plus a durable "was cast using
teamwork" fact readable at resolution.

The **payoff half is nearly free**. Modelling it as
`KeywordAbility.OptionalAdditionalCost(additionalCost = …, branchesEffect = true, displayPrefix =
"Teamwork", keyword = Keyword.TEAMWORK)` (`mtg-sdk/.../scripting/KeywordAbility.kt`) puts it on the
existing kicker/bargain rail, so `Conditions.WasKicked` already answers it and
`modal(dynamicChooseCount = DynamicAmount.Conditional(wasTeamwork, 2, 1))` already expresses "choose
one; if cast using teamwork, choose both instead" — no new modal machinery. The clean version adds a
dedicated `ChoiceSlot.TEAMWORK` + `Conditions.TeamworkWasPaid` facade mirroring `BARGAINED` /
`WEB_SLUNG`, and `Keyword.TEAMWORK` for reminder text.

The **genuine gap is the cost atom**: "tap any number of creatures you control with total power N or
more". `CostAtom.TapPermanents` (`mtg-sdk/.../scripting/costs/CostAtom.kt`) is a *fixed* count;
`CostAtom.VariablePermanents` is variable-count but its `PermanentCostAction` covers only
`EXILE`/`SACRIFICE` and its `VariableCostMeasure` only `TOTAL_MANA_VALUE`/`COUNT`, with the threshold
expressed as `minCount`. Needed: `PermanentCostAction.TAP`, `VariableCostMeasure.TOTAL_POWER`, and a
`minMeasure` threshold field, plus the matching payment/enumeration branches.

**The selection itself already exists** in the engine for Crew and Saddle — "tap any number of
creatures you control with total power N or greater" is precisely the crew payment
(`rules-engine/.../legalactions/enumerators/CrewEnumerator.kt`, `CrewSaddleContributorsComponent`,
and the total-power sum in `mechanics/mana/CostCalculator.kt`). It is wired as an
*activated-ability* cost rather than a spell's optional additional cost; the work is re-exposing it
on the cast rail, not building it.

Blocked cards: **Agent Maria Hill** [2] · **Helicarrier Strike** [15] · **Murdock's Crusade** [24] ·
**Atlantis Attacks** [46] · **We Say Thee Nay!** [82] · **Cruel Alliance** [92] · **Too Evil to Stay
Dead** [118] · **Widow's Bite** [122] · **HULK SMASH!** [135] · **Repulsor Blast** [150] · **Team
Tactics** [155] · **Earth's Mightiest Heroes** [165] · **Go Nuts!** [168].

**Agent Maria Hill** [2] needs one thing more: "whenever she becomes tapped **to pay a teamwork
cost**". `TappedEvent` carries only `tappedById` with no cause, so a teamwork tap is
indistinguishable from a crew, saddle, attack, or mana tap. Add a tap-reason field on the event (set
in the teamwork payment path) and a matching predicate on `EventPattern.TapEvent`.

## Shield counters — 1 card ⛔

> Captain America enters with a shield counter on him. *(If he would be dealt damage or destroyed,
> remove a shield counter from him instead.)*

Not implemented at all. `docs/card-sdk-language-reference.md` §16 lists `shield` among printed
counter kinds, but there is no `CounterType.SHIELD`, no `Counters.SHIELD`, and no engine handling
anywhere. Needed: the enum constant + string constant in `mtg-sdk/.../core/CounterType.kt`, plus the
built-in replacement per CR 122.1e.

The **stun counter is the exact structural precedent** — a counter with an inherent rule wired at a
central chokepoint (`untapOrConsumeStun` in `rules-engine/core/UntapHelpers.kt`, invoked from
`BeginningPhaseManager`, `TapUntapExecutor`, and the sacrifice/pay resumer). Shield needs the same at
the damage-application site (`DamageUtils.dealDamageToTarget`) and the destroy path (destroy executor
plus the lethal-damage state-based action).

Blocked card: **Captain America, Super-Soldier** [9]. Its second clause is already fine —
`GrantHexproofToController` + `GrantKeyword(HEXPROOF, Heroes)` under a `ConditionalStaticAbility`
gated on `Conditions.SourceHasCounter`.

## Equip worthy — 1 card ⛔

> Equip worthy {1} *(A creature is worthy if it's a legendary non-Villain that's red and/or white.)*

An equip ability whose attach target is filtered. `equipAbility(cost, genericCostReduction)`
(`mtg-sdk/.../dsl/CardBuilder.kt`) hard-codes `TargetCreature(filter = TargetFilter.CreatureYouControl)`
with no override, so the restriction cannot be authored. Needed: a `targetFilter: TargetFilter? = null`
parameter on `equipAbility` (the hand-rolled `activatedAbility { isEquipAbility = true }` escape hatch
would also work if it accepted the filter), plus a "worthy" reminder-text / keyword-display entry.

Same card, second gap: "Double all damage equipped creature would deal" wants
`DoubleDamage(appliesTo = DamageEvent(source = <the equipped creature>))`, but `SourceFilter`
(`mtg-sdk/.../scripting/events/EventFilters.kt`) has only `EnchantedCreature` on the source side —
`RecipientFilter` has an `EquippedCreature` case and `SourceFilter` does not. A one-line mirror.

Blocked card: **Mjölnir, Hammer of Thor** [146].

## Copy-with-exceptions: name, added types, longer durations — 3 cards ⛔

`EachPermanentBecomesCopyOfTargetEffect` (`mtg-sdk/.../scripting/effects/CopyEffects.kt`) exposes only
`addedKeywords` / `powerOverride` / `toughnessOverride` / `retainActivatingAbility` as copy exceptions,
and its executor honours only `Duration.Permanent` / `EndOfTurn` / `UntilNextEndStep` — silently
degrading anything else to permanent. Three additions, each of which already exists on the *token*
sibling `CreateTokenCopyOfTargetEffect`, so this is convergence rather than one-offs:

1. `nameOverride` (exists on the ETB replacement `ReplacementEffect.EntersAsCopy`, not on this path).
2. `addedSupertypes` / `addedSubtypes` / `addedCardTypes` — for "he's a legendary Human Mercenary
   Villain creature **in addition to** its other types", and for "except it isn't legendary" (which
   needs the *removal* direction, `removedSupertypes`; without it the copy dies to the legend rule).
3. `Duration.UntilYourNextTurn` support in the copy-revert path — a `RevertCopyAtYourNextTurnComponent`
   sibling to the two existing revert markers.

Blocked cards: **Shuri, Wakandan Inventor** [75] (needs `removedSupertypes`) · **Absorbing Man** [199]
(needs all three) · **Taskmaster, Mercenary Mimic** [232] (needs all three).

## Improvise (CR 702.126) — 2 cards ⛔

No `IMPROVISE` anywhere; `Keyword` has `CONVOKE`, `DELVE`, `AFFINITY` but not improvise. The closest
analogue is **waterbend**, described in the SDK as literally "a generic-only convoke+improvise" —
improvise is a strict subset of it (artifacts only), and `AlternativePaymentChoice.waterbendPermanents`
already taps artifacts *and* creatures for `{1}` each. Needed: `Keyword.IMPROVISE`; an
`improvisedArtifacts: Set<EntityId>` field on `AlternativePaymentChoice`; an `applyImprovise` branch in
`AlternativePaymentHandler` mirroring `applyWaterbend`/`applyConvoke`; and the field threaded through
`GameAction` / `PendingDecision` / `ManaContinuations` / `LegalAction` / `LegalActionEnricher` /
`CastSpellEnumerator` plus client UI and AI payment heuristics — the same surface convoke occupies.

Blocked cards: **Ironheart, Clever Champion** [60] · **Arc Reactor** [243]. Ironheart's second line
("Noncreature spells you cast have improvise") then needs no extra work —
`SpellStaticAbilities.GrantsKeywordToSpells` is built for exactly this and already handles
cost-modifying keywords.

## Ability-source predicate on stack targets — 2 cards ⛔

Abilities on the stack carry no `CardComponent`, and `PredicateEvaluator.matchesCardPredicate`
(`rules-engine/.../handlers/PredicateEvaluator.kt`) bails out on them, so `GameObjectFilter.Artifact`
is always false for an ability. `Targets.ActivatedOrTriggeredAbilityYouControl` and
`Effects.CopyTargetSpellOrAbility` both exist (precedent `fin/cards/GogoMasterOfMimicry.kt`) — what is
missing is restricting the target by its **source**. Needed: a `CardPredicate.AbilitySourceMatches(filter)`
resolved in the evaluator's stack branch against the ability's `sourceEntityId`, matched with
last-known information since the source may have left. The concept already exists engine-side as the
static `CantBeTargetedBySourceTypeAbilities`.

Blocked cards: **Echo, Perceptive Prodigy** [51] (creature source) · **Scientist Supreme of A.I.M.**
[225] (artifact source).

## Ward with a non-listed cost — 2 cards ⛔

`WardCost` (`mtg-sdk/.../scripting/effects/StackEffects.kt`) has exactly six variants: `Mana`, `Life`,
`DynamicLife`, `Discard`, `Sacrifice`, `Composite`. `Composite` is **AND** (Gisa's "Ward—{2}, Pay 2
life"); there is no disjunction and no arbitrary-effect variant. Each addition needs a new sealed case
plus a branch in `WardCounterEffectExecutor` (currently exhaustive) and description strings in
`KeywordAbility.kt` / `KeywordStaticAbilities.kt` / `StackEffects.kt`:

- `WardCost.PlayerCounters(counterType, amount)` — "Ward—Get five poison counters." `Counters.POISON`
  and player-scoped counter placement already work; only the ward-cost wrapper is missing.
  → **The Serpent Society** [226]
- `WardCost.Choice(options)` — "Ward—Discard a card or pay {2}", an OR-disjunction with a pick-one
  prompt; model it on the existing `AdditionalCost.Choice`. → **Titania, Rugged Rumbler** [235]

Both cards' other halves are already expressible (Serpent Society's deathtouch-dies edict; Titania's
*additional cost* via `Costs.additional.DiscardOrPay("{2}")`, precedent
`spm/cards/PumpkinBombardment.kt`).

## Missing keyword counters: haste, menace — 1 card ⛔

Nine of the eleven keyword counters exist and are wired. `HASTE` and `MENACE` are absent. Three lines
each: a `CounterType` entry + a `Counters` const in `mtg-sdk/.../core/CounterType.kt`, and an entry in
`KEYWORD_COUNTER_MAP` (`rules-engine/.../mechanics/layers/StateProjector.kt`).

Blocked card: **Super-Adaptoid** [250]. Everything else on it composes today.

## One-off blockers

Each of these is a single card needing one specific addition.

### Batched counters-placed trigger — **Invisible Woman, Sue Storm** [17]
"Whenever you put one or more +1/+1 counters on one or more **other Heroes** you control" is a CR
603.2c batch: one payoff per simultaneous placement. `Triggers.countersPlacedOn(...)` fires once **per
receiving permanent**, so it over-fires on exactly the plays this set is built around (Phil Coulson's
tap ability, Origin of the Avengers III). Needed: a `batch` flag on `CountersPlacedEvent`
(`mtg-sdk/.../scripting/events/EventFilters.kt`) plus a `TriggerDetector.detectCounterPlacementBatchTriggers`.
Direct analogues already exist as `OneOrMoreBecomeTapped` (`TapEvent(batch = true)` →
`detectTapBatchTriggers`) and `OneOrMorePermanentsEnter`.

### Per-permanent "you put counters on it this turn" memory — **Kid Loki** [63]
No turn-scoped counter-placement memory exists. `StatePredicate` has `EnteredThisTurn`,
`AttackedThisTurn`, `HasCounter`, `IsModified` — nothing about *when* counters arrived. Needed: a
`CountersPutOnThisTurnComponent` (mirroring `CardsDrawnThisTurnComponent`) written at the
counter-placement chokepoint and cleared by `TurnManager.startTurn`, plus
`StatePredicate.HadCountersPutOnThisTurn(counterType, placedBy)` and an
`ObjectFilter.hadCountersPutOnThisTurn()` builder. The "*you've* put" attribution is free —
`CountersPlacedEvent.placedBy` already carries the placer per CR 122.6a.

### Replacing a keyword action (connive) — **Leader, Super-Genius** [64]
"If a creature you control would connive, instead you draw a card, then that creature connives."
Connive is a *composed effect* (`Patterns.Hand.connive` / `Effects.Connive`), not an event the
replacement system can see; `ReplacementEffect` has no keyword-action variants at all (its nearest
neighbours are `ModifyDrawAmount`, `ModifyMillAmount`, `ModifyExplore`). Needed: either a
`ConniveEvent` `EventPattern` + `ReplacementEffect.ModifyConnive(prefixEffect)` read by the connive
executor, or a `ConniveModifier` static consulted at the same point. The card's second ability is fine
today.

### "Becomes the target of an ability you control" — **Loki, God of Mischief** [65]
Two independent gaps in `EventPattern.BecomesTargetEvent`: (1) **player targets are never emitted** —
`StackResolver` documents that it emits the event for permanent and spell targets only; needs emission
plus a player-aware branch in `TriggerMatcher.matchesBecomesTargetTrigger`; (2) **no abilities-only
filter** — the event has `spellsOnly` but no inverse. `byYou` and `firstTimeEachTurn` already exist.

### Counting coloured mana symbols in one object's cost — **Namor the Sub-Mariner** [69]
`EntityNumericProperty` exposes `ManaValue`, `ColorCount`, `SubtypeCount` — no pip count;
`DynamicAmount.DevotionTo` counts pips only across a player's whole battlefield. Needed:
`EntityNumericProperty.ColoredManaSymbolCount(colors)` reusing `DevotionTo`'s hybrid/Phyrexian rules,
plus `CardPredicate.ManaCostContainsSymbol(color)` for the trigger's filter (`withColor(BLUE)` tests
the card's *colour*, which is not the same thing). Namor's dynamic power is fine.

### Aggregate-metric cost + capped free cast — **Baron Helmut Zemo** [87]
Three gaps stack. (1) "Exile any number of black cards from your graveyard with **fifteen or more black
mana symbols** among their mana costs" — costs select by count and filter, never by a summed per-card
metric; structurally the same selection crew uses for total power, over the missing
`ColoredManaSymbolCount` above. (2) `CastAnyNumberFromCollectionWithoutPayingCostEffect`
(`mtg-sdk/.../scripting/effects/LibraryEffects.kt`) has no `maxCasts` field, so "cast up to three of the
copies" can't be bounded. (3) Not a blocker: the boast window is already
`ActivationRestriction.OncePerTurn` + `Conditions.SourceAttackedThisTurn`; only a cosmetic
`Keyword.BOAST` is absent.

### Capped free cast — **Doom Reigns Supreme** [96]
The same missing `maxCasts` on `CastAnyNumberFromCollectionWithoutPayingCostEffect` ("cast up to two
spells from among the exiled cards"). Everything else on the card — including the plan-counter
threshold — composes today.

### Mana restricted to equip abilities — **Ronin, Shadow Stalker** [112]
"Spend this mana only to cast Equipment spells **or activate equip abilities**". `ManaRestriction` has
`SubtypeSpellsOnly(setOf("Equipment"))` for the spell half and `AbilityActivationOnly` for *any*
activated ability; `AnyOf(...)` of the two would wrongly pay for every activated ability in play.
Needed: `ManaRestriction.EquipActivationOnly` plus an `isEquipActivation` flag threaded onto
`SpellPaymentContext` (the `ActivatedAbility.isEquipAbility` marker it would read already exists).
Everything else on the card is available.

### "Until the end of your next turn" continuous duration — **Evil's Thrall** [128]
`Duration` has `EndOfTurn`, `UntilYourNextTurn` (which ends at the *beginning* of your next turn — a
full turn short), `UntilNextEndStep` and `UntilYourNextUpkeep`, but no "until the end of your next
turn". The turn-keyed window exists only for play permissions (`MayPlayExpiry.UntilEndOfNextTurn`), not
for continuous effects, so `GainControlEffect` cannot express the conditional half. Needed:
`Duration.UntilEndOfYourNextTurn` plus its expiry sweep in `CleanupPhaseManager`.

### Repeatable optional payment feeding a modal — **Hawkeye, Master Marksman** [130]
"You may pay {1} **up to three times**. When you do, choose up to that many." `MayPayManaEffect` /
`ReflexiveTriggerEffect` handle a *single* optional payment; `RepeatDynamicTimesEffect` repeats a fixed
count. Nothing loops an optional payment, stops on decline, and publishes the number of times paid.
Per-mode `additionalManaCost` on `ModalEffect` is not an alternative — it is honoured only by
`CastSpellEnumerator` / `CastSpellHandler` at cast time, never during a triggered ability's resolution.
Needed: `RepeatOptionalPaymentEffect(cost, maxTimes, storeCountAs)` whose stored count feeds
`ModalEffect.dynamicChooseCount` (`chooseUpToDynamic` already accepts any `DynamicAmount`).

### Targeted proliferate — **Powerful Broker** [179]
`ProliferateEffect` is a bare `data object` with no target and no cap
(`mtg-sdk/.../scripting/effects/CounterEffects.kt`); the executor picks its own permanents at
resolution, so it cannot express a *targeted* single-object proliferate (which is respondable and
respects hexproof). Needed: a `target: EffectTarget? = null` field plus a branch in
`ProliferateExecutor` that skips the selection continuation when a target is supplied, and a "target
permanent or player" `TargetRequirement`.

### "Activate abilities as though they had haste" — **Shang-Chi, Master of Kung Fu** [187]
No static, keyword, or engine hook for this anywhere. The summoning-sickness gate for `{T}` abilities
is checked inline against `SummoningSicknessComponent` + `hasHaste` in `ManaSolver` and the ability
enumerators. Needed: a `MayActivateAbilitiesAsThoughHasty(filter: GroupFilter)` static in
`MiscStaticAbilities.kt` read at each of those sites. `GrantKeyword(HASTE, …)` is **functionally
wrong** — it would also lift the attack restriction. The card's mana ability is already fine.

### One-shot "next spell you cast is free" — **World War Hulk** [197]
The pending-rider family in `mtg-sdk/.../scripting/effects/StackEffects.kt` has
`CopyNextSpellCastEffect`, `MakeNextSpellUncounterableEffect` and `GrantNextSpellAffinityEffect` — but
no free-cast sibling. Needed: `CastNextSpellFreeEffect(spellFilter)` + a `PendingNextSpellFreeCast`
state record mirroring `rules-engine/.../state/PendingNextSpellAffinity.kt` (held on `GameState`,
consumed in `CastSpellHandler`, honoured by `CostCalculator.hasFreeCastPermission`). The existing static
`MayCastWithoutPayingManaCost` is battlefield-resident with no one-shot consumption, so a Saga chapter
cannot use it — even with `oncePerTurn` it would keep granting the free cast on chapters II and III.

### Disjunctive activated-ability cost — **Bullseye, Death Dealer** [209]
"Sacrifice an artifact **or** discard a nonland card" as an activation cost. `AbilityCost` has no
`Choice`/`AnyOf` variant, and neither does `CostAtom`. The two disjunctive cost types that exist serve
other slots: `PayCost.Choice` (consumed only by resolution-time `PayOrSufferEffect`) and
`AdditionalCost.Choice` (spell casts only). Needed: `AbilityCost.Choice(options: List<AbilityCost>)`
plus the payer/enumerator branch that offers the affordable options. The card's ETB half **is**
expressible today via `ReflexiveTriggerEffect` over `Effects.ChooseAction` with `FeasibilityCheck`s.

### Per-permanent "first time it became tapped this turn" — **Captain America, Living Legend** [210]
`EventPattern.TapEvent` carries only `filter`, `batch`, `tapper`, and no per-entity tap history exists.
The `firstTimeEachTurn` gate exists on `LifeGainEvent`, `BecameSaddledEvent`, `CountersPlacedEvent` and
`BecomesTargetEvent`, each backed by an event-specific `firstThisTurn` flag computed in
`TriggerMatcher`. Needed: a `BecameTappedThisTurnComponent` (cleared in `CleanupPhaseManager`), a
`firstThisTurn` field on `TappedEvent`, a `firstTimeEachTurn` field on `TapEvent`, and the matching
`TriggerMatcher` branch. The "during your turn" half is already `Conditions.IsYourTurn`.

### Linked exile from hand + return to origin zone — **Cloak and Dagger, Entwined** [211]
`ExileUntilLeavesEffect` accepts battlefield permanents and graveyard cards only — every other zone is
explicitly ignored. The hand branch *can* be linked via a
`GatherCards → SelectFromCollection → MoveCollectionEffect(linkToSource = true)` pipeline, and the
either/or choice via `Effects.ChooseAction`. **The real gap is the return side**:
`ReturnLinkedExileToHand` and `ReturnLinkedExileUnderOwnersControl` both act on the whole linked pile,
and nothing returns each card to the zone it came from. Needed: a `ReturnLinkedExileToOriginZone` effect
(or an origin-zone field on `LinkedExileComponent`), plus lifting the hand-zone restriction on
`ExileUntilLeaves` so the card reads as one primitive.

### Two additions on one card — **Storm, Windrider** [230]
1. **"those creatures gain flying"** — `Triggers.youCastSpellTargeting(filter)` exists, but nothing
   names *the targets of the triggering spell*. `EffectTarget` has `TriggeringEntity`, `CardSource` has
   `ChosenTargets` (this effect's own targets) — neither reaches the spell's targets. Cleanest fix: have
   the `SpellCastPredicate.TargetsMatching` matcher record the matching target ids into
   `TriggerContext.capturedEntityIds` (already plumbed to `PipelineState.TRIGGER_CAPTURED_COLLECTION` by
   `StackResolver`), after which the card is a plain `ForEachInCollection(…, GrantKeyword(FLYING, …))` —
   no new effect type.
2. **"Creatures with flying can't attack you"** — the only defender-relative attack restriction is
   `CantBeAttackedWithout(requiredKeyword, attackerFilter)`, which is *inverted*; `CantAttack(filter)` is
   global. Needs either a nullable `requiredKeyword` or a new `CantBeAttackedBy(attackerFilter)` static
   plus its branch in the attack-legality check. (The "or block creatures you control" half is already
   `CantBeBlockedBy`.)

### Per-turn "dealt damage this turn" predicate — **Red Guardian, Super-Soldier** [34]
"Destroy target creature an opponent controls **that dealt damage this turn**" — a per-turn record of
damage *inflicted*. Three near-misses, none usable:
`StatePredicate.WasDealtDamageThisTurn` is the **passive** direction (damage received);
`StatePredicate.HasDealtDamage` is the right direction but wrong duration —
`HasDealtDamageComponent` is documented and implemented as persisting for the permanent's
battlefield lifetime, never cleared at end of turn
(`rules-engine/.../battlefield/BattlefieldComponents.kt:998-1004`, and absent from
`CleanupPhaseManager`'s per-turn strip list), so it would let Red Guardian kill anything that ever
dealt damage in any earlier turn; and the remaining damage-history predicates
(`HasDealtCombatDamageToPlayer`, `DealtCombatDamageToSourceControllerThisTurn`,
`ControllerDealtCombatDamageBySourceThisTurn`) are combat- and recipient-specific, missing noncombat
damage and damage to creatures. Needed: `StatePredicate.DealtDamageThisTurn` plus a per-turn
`DealtDamageThisTurnComponent` stamped in `DamageUtils` / `CombatDamageManager` alongside
`HasDealtDamageComponent`, cleared in `CleanupPhaseManager`, and wired into `PredicateEvaluator`,
`AffectsFilterResolver`, `TriggerMatcher`, `BeginningPhaseManager` and `Serialization`.

### "Do this only once each turn" — an effect cap, not a trigger cap — **Jennifer Walters** [18]
> Whenever a creature you control is dealt damage, you may have The Sensational She-Hulk deal that
> much damage to any target. **Do this only once each turn.**

Per CR 603.2 the ability triggers **once per damaged creature**; "Do this only once each turn" limits
how often the *effect* may be applied. So in a multi-block, every damaged creature puts a trigger on
the stack and the controller declines until the one carrying the biggest damage number — the whole
point of the card.

Modelling it as `oncePerTurn = true` (a **trigger** cap) fires only for the *first* creature dealt
damage and never offers the rest, so a big hit later in the same combat is unreachable. **Implemented
then removed from this branch** for that reason; it needs a per-turn *effect* budget — an
`effectOncePerTurn` flag on `TriggeredAbility` (or a turn-scoped "already applied" marker the effect
checks and sets) so all instances trigger while at most one resolves its effect.

Do not confuse this with the other wording: "**This ability triggers** only once each turn"
(Crossbones [91], Moon Girl [223], Knight of Wundagore [175], Ant-Man [201]) *is* a trigger cap and is
correctly `oncePerTurn = true` today.

**Also removed for the same defect:** **Baron Strucker, HYDRA Overlord** [88] — "Whenever another
Villain you control enters, you may have it connive. **Do this only once each turn.**" With
`oncePerTurn = true`, when two Villains enter together only the first triggers, so you cannot pick
which one connives.

Both cards become implementable the moment the per-turn *effect* budget exists; neither needs
anything else.

### Power-only dynamic CDA granted for a duration — **Ms. Marvel, Kamala Khan** [67]
> Embiggen Fist — Whenever you cast a spell that targets a creature you control, draw a card. Until
> end of turn, Ms. Marvel gains "**Ms. Marvel's base power is equal to the number of cards in your
> hand.**"

The granted clause is a **characteristic-defining ability**: base power must keep tracking hand size
for the rest of the turn. `Effects.SetBasePower` is a one-shot resolution-time *set* —
`SetBaseStatsEffect` documents its `power` as "evaluated at resolution time" and is deliberately
distinct from the projector's `SetPowerToughnessDynamic`, which is "re-evaluated per affected entity
at projection time". So her power froze at whatever the hand was when the trigger resolved.
Reproduced: hand 8 → power 11 (8 base + Giant Growth's 3); after a draw, hand 9 → power still 11.

**Implemented then removed from this branch.** The right shape is
`Effects.GrantStaticAbility(<power-only dynamic CDA>, EffectTarget.Self, Duration.EndOfTurn)` —
`GrantStaticAbility` already exists, but there is no power-only dynamic CDA to hand it:
`SetBasePowerToughnessDynamicStatic` (`mtg-sdk/.../scripting/StatsStaticAbilities.kt`) sets **both**
stats from one `DynamicAmount`, which would clobber her printed toughness of 4. Needed: a
`SetBasePowerDynamicStatic` mirroring the existing toughness-only `SetBaseToughnessForCreatureGroup`,
plus its `StaticAbilityHandler` branch onto the projector's existing `SetPowerToughnessDynamic` path.
Narrow and reusable — any "power is equal to X" grant wants it.

Her other two lines are fine today: `NoMaximumHandSize` and
`Triggers.youCastSpellTargeting(Creature.youControl())`.

### Cost reduction reading the source's own characteristic — **The Scarlet Witch** [151]
"Instant and sorcery spells you cast with mana value 4 or greater cost {X} less to cast, where X is
The Scarlet Witch's power." The obvious recipe —
`ReduceGenericBy(GreatestPropertyAmongPermanentsYouControl(Power, Any.named("The Scarlet Witch")))` —
compiles but **always reduces by 0**. `CostCalculator.greatestPropertyAmongMatching`
(`rules-engine/.../mechanics/mana/CostCalculator.kt:524`) matches battlefield permanents with a
stripped-down `matchesBattlefieldPredicate` (same file, ~line 768) that handles only
`IsCreature` / `IsArtifact` / `IsEnchantment` / `IsLand` / `IsPermanent` / `HasSubtype` and falls
through to `else -> false` for everything else, including `CardPredicate.NameEquals`. It ignores
`statePredicates` too, so `.sourceItself()` is equally inert. The card would read right and resolve
wrong. Either fix works:
1. A `CostReductionSource.SourceProperty(EntityNumericProperty.Power)` evaluated against
   `context.sourceId` — the exact-fidelity option; or
2. extend `CostCalculator.matchesBattlefieldPredicate` to handle `CardPredicate.NameEquals` (the full
   matcher one function over, ~line 994, already does), which makes the recipe work via the legend rule.

No existing `CostReductionSource` gets the semantics right — the subtype-based approximations
("greatest power among Warlocks/Heroes/Mutants you control") over-reduce whenever another such
creature is bigger.

### Condition-gated flash grant — **Captain Mar-Vell, Space-Born** [12]
"Cosmic Awareness — As long as an opponent has cast a spell this turn, you may cast spells as though
they had flash." **No new SDK vocabulary is needed** — `GrantFlashToSpellType` and
`Conditions.CompareAmounts(DynamicAmounts.spellsCastThisTurn(Player.EachOpponent), GTE, 1)` both
exist. The blocker is plumbing: both flash-permission scans match the raw ability type and never
unwrap the `ConditionalStaticAbility` that `staticAbility { condition = … }` produces —
`rules-engine/.../handlers/actions/spell/CastZoneResolver.kt:551` and
`rules-engine/.../legalactions/utils/CastPermissionUtils.kt:528` both do
`for (ability in def.script.staticAbilities) { if (ability is GrantFlashToSpellType) … }`. A gated
grant is therefore *silently inert* — it never grants flash at all, rather than granting it
conditionally. Fix: route both loops through the existing
`CastPermissionUtils.activeStaticAbility(...)` helper (`CastPermissionUtils.kt:418`), which already
performs exactly this unwrapping for equip and play-from-top permissions. Roughly a two-line change,
and it likely fixes other gated permissions that are inert today.

### Damage replacement that heals previously-marked damage — **Wolverine, Fierce Fighter** [240]
"If damage would be dealt to Wolverine, instead that damage is dealt, but all other damage already
dealt to him is healed." Two gaps: (a) no SDK effect removes marked damage on demand — the
`without<DamageComponent>()` helper is reachable only through `RegenerateEffect` /
`RemoveDamageShieldEffect`, so an `Effects.RemoveAllDamage(target)` one-shot is needed; (b) every damage
replacement in `ReplacementEffect.kt` *changes* the damage (`PreventDamage`, `RedirectDamage`,
`DoubleDamage`, `ModifyDamageAmount`, `CapDamage`, `SetMinimumDamage`, `ReplaceDamageWith*`) — none lets
the damage through unchanged while running a rider; that shape exists only for zone changes
(`OnEnterRunEffect`). A triggered ability is **not** a substitute: state-based actions would see the
accumulated total and kill Wolverine before the trigger resolved.

---

## Small non-blocking additions worth folding in

These do not block any card outright, but the triage surfaced them as cheap correctness or fidelity
wins in whichever PR next touches the area:

- `Subtype.SYNTHEZOID` is missing from the constant list and `ALL_CREATURE_TYPES`. No MSH booster card
  needs it (Viv Vision is typed `Robot Hero`), but Vision, Synthezoid Avenger will.
- A `notAttacking()` builder on `ObjectFilter`/`TargetFilter`. Spider-Man, To the Rescue [228] currently
  hand-rolls `StatePredicate.Not(StatePredicate.IsAttacking)`, and the existing
  `mir/cards/Alarum.kt` silently drops its nonattacking restriction.
- `ReplacementEffect.ModifyCounterPlacement` has no `placedByYou` flag (its sibling
  `DoubleCounterPlacement` does). Doc Samson [164] is therefore modelled as the Winding Constrictor "if
  counters would be put" wording rather than the printed "**If you would put** …".
- `Conditions.SourceReceivedCounterThisTurn` matches any counter kind, not a specific one. Beast [206]
  reads "one or more **+1/+1** counters"; a `counterType` parameter would make it exact.
- `Keyword.EXTORT` does not exist. The Kingpin of Crime [220] composes it exactly as
  `Triggers.YouCastSpell` + `MayPayManaEffect("{W/B}", DrainLife(1))`; promote it to a real keyword when
  a second extort card lands.
- `Keyword.BOAST` does not exist either; the activation window composes from
  `ActivationRestriction.OncePerTurn` + `Conditions.SourceAttackedThisTurn`.
