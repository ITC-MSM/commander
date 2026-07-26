# Marvel's Spider-Man (SPM) — Missing Mechanics

Cards from SPM that **cannot** be implemented with the current engine/SDK, grouped by the
missing mechanic they need. Each mechanic is `add-feature` territory (a new SDK primitive,
keyword, or engine capability) — not pure card authoring.

Supported today (confirmed against the `Keyword` enum + SDK): connive, saga, convoke, kicker,
vehicles/crew, surveil, fight, copy-spell, copy-ability, play-from-top-of-library, impulse
exile-and-play, max-hand-size modification, transform DFCs, Food/Treasure. This file is updated
as the loop discovers new blockers.

---

## Web-slinging (new alternative cost + additional return-a-tapped-creature cost)

> Web-slinging {cost} *(You may cast this spell for {cost} if you also return a tapped
> creature you control to its owner's hand.)*

A brand-new keyword: an **alternative mana cost** bundled with an **additional cost**
("return a tapped creature you control to its owner's hand"). Not implemented — no card in
the corpus uses it, and there is no `KeywordAbility.WebSlinging` / alternative-cost-with-
creature-return primitive. Several cards further **key off** having been cast with web-slinging
(Scarlet Spider gains counters equal to the returned creature's mana value; Spiders-Man's ETB
checks "if they were cast using web-slinging"), which needs a "was-cast-with-web-slinging"
spell-cast flag the ETB/enters triggers can read.

Blocked cards:
- **Arachne, Psionic Weaver** [2] — `{2}{W}`, Web-slinging `{W}` (also: look at opp hand, choose a card type, that type costs {1} more)
- **Spider-Man, Web-Slinger** [16] — `{2}{W}`, Web-slinging `{W}` (vanilla otherwise)
- **Spider-UK** [17] — `{3}{W}`, Web-slinging `{2}{W}`
- **Spider-Man, Brooklyn Visionary** [115] — `{4}{G}`, Web-slinging `{2}{G}`
- **Spiders-Man, Heroic Horde** [117] — `{1}{G}`, Web-slinging `{4}{G}{G}`; ETB checks "if cast using web-slinging"
- **Scarlet Spider, Ben Reilly** [142] — `{1}{R}{G}`, Web-slinging `{R}{G}`; enters with X counters = returned creature's mana value
- **Silk, Web Weaver** [145] — `{2}{G}{W}`, Web-slinging `{1}{G}{W}`
- **Spider-Man India** [151] — `{3}{G}{W}`, Web-slinging `{1}{G}{W}`
- **Spider-Sense** [46] — `{1}{U}`, Web-slinging `{U}` (counter instant/sorcery/triggered ability)
- **Peter Parker // Amazing Spider-Man** [10] — transform DFC whose *back face grants* web-slinging to your legendary spells (also blocked on this)

## Harness / Infinity Stone ∞ ability

> `{cost}, {T}, Exile a creature you control: Harness <this>. (Once harnessed, its ∞ ability
> is active.)` — ∞ — <ongoing ability>

A new designation: an activated "harness" cost that permanently switches on a dormant `∞`
ability. No `Harness` / `InfinityStone` primitive exists (a permanent-state flag that gates a
static/triggered ability). `add-feature` territory.

Blocked cards:
- **The Soul Stone** [66] — `{1}{B}` Legendary Artifact — Infinity Stone; harness → `∞` upkeep reanimation

## Mayhem (new keyword — self graveyard-cast gated on "you discarded this card this turn")

> Mayhem {cost} *(You may cast this card from your graveyard for {cost} if you discarded it
> this turn. Timing rules still apply.)*

Not implemented. There is **no `Keyword.MAYHEM`**, no `KeywordAbility.Mayhem`, and — the
load-bearing gap — the engine does not track **"cards you discarded this turn"**, so there is
no condition to gate the graveyard-cast on (only `discardedAsCostCards`, i.e. cards discarded
to pay a spell's own additional cost). The closest primitives (`Flashback`, `Harmonize`,
`Warp`, `MayCastFromGraveyard`) all let you cast from the graveyard **any** time the card is
there — dropping the "only the turn you discarded it" gate would make every Mayhem card
materially stronger and wrong. `add-feature` scope: (1) turn-scoped discarded-this-turn
tracking + a `YouDiscardedThisCardThisTurn` condition; (2) a `Keyword.MAYHEM` /
`KeywordAbility.Mayhem(cost)` self graveyard-cast alternative-cost primitive (permanent just
enters — no exile-on-resolve) wired into the cast-from-zone enumerator + `CastSpellHandler`.

Blocked cards:
- **Swarm, Being of Bees** [69] — `{2}{B}` Flash Flying, Mayhem `{B}`
- **Spider-Islanders** [91] — `{3}{R}` (vanilla), Mayhem `{1}{R}`
- **Raging Goblinoids** [85] — `{4}{R}` Haste, Mayhem `{2}{R}` (5/4)
- **Electro's Bolt** [77] — `{2}{R}` deal 4 to a creature, Mayhem `{1}{R}`
- **Prison Break** [61] — `{4}{B}` reanimate + counter, Mayhem `{3}{B}`
- **Sandman's Quicksand** [63] — `{1}{B}{B}` mass -2/-2 (mayhem-cast → opponents only), Mayhem `{3}{B}`
- **Scarlet Spider, Kaine** [143] — `{B}{R}` Menace + discard→counter, Mayhem `{B/R}`
- **Carnage, Crimson Chaos** [125] — `{2}{B}{R}` Trample + ETB reanimate, Mayhem `{B}{R}`
- **Chameleon, Master of Disguise** [27] — `{3}{U}` enter-as-copy, Mayhem `{2}{U}` (also needs clone-on-ETB — verify)
- **Rocket-Powered Goblin Glider** [172] — `{3}` Equipment (attach if cast from gy), Mayhem `{2}`
- **Ultimate Green Goblin** [157] — `{1}{B/R}{B/R}` upkeep discard+Treasure, Mayhem `{2}{B/R}`
- **Oscorp Industries** [182] — land, Mayhem (play land from graveyard if discarded this turn)
- **Norman Osborn // Green Goblin** [39] — transform DFC; back's "Goblin Formula" grants Mayhem to every nonland card in your graveyard (also blocked on transform + this grant)

Also blocked by the **discarded-this-turn tracking** half of this gap (a `CardsDiscardedThisTurnComponent` accumulator + `DynamicAmount.CardsDiscardedThisTurn`), independent of the Mayhem keyword itself:
- **Green Goblin, Revenant** [130] — `{3}{B}{R}` Flying/deathtouch; "Whenever Green Goblin attacks, discard a card. Then **draw a card for each card you've discarded this turn**." (Flying/deathtouch + the discard are fine; the draw-per-discarded-this-turn count is blocked.)

## Riot (keyword — enters with your choice of a +1/+1 counter or haste)

> Riot *(This creature enters with your choice of a +1/+1 counter or haste.)*

Not in the `Keyword` enum and no ETB "choose counter or haste" primitive that also grants a
**projectable** Riot keyword. Spider-Punk further needs to **grant riot to other Spiders**,
which requires Riot to exist as a grantable keyword. `add-feature` scope.

Blocked cards:
- **Spider-Punk** [92] — `{1}{R}` Riot; "Other Spiders you control have riot"; also "Spells and abilities can't be countered" + "Damage can't be prevented" (verify those two independently)

## "Modified" state on a leaves-the-battlefield (last-known-information) trigger

> Whenever a **modified** creature you control **leaves the battlefield**, …

The `IsModified` state-predicate (CR 700.4 — has an Equipment/controlled Aura attached, or a
counter) works fine as a *static/targeting* filter, but it is **not gated** on a zone-change
(leaves/dies) trigger. In `rules-engine/.../event/TriggerMatcher.kt`,
`matchesStatePredicateForZoneChangeTrigger` has last-known-information cases for `HasCounter`,
`HasAnyCounter`, `HasGreatestPower`, etc., but `IsModified` (and siblings `IsEquipped` /
`IsSaddled`) fall through to the "don't gate — return true" path, so the trigger fires for
**every** creature that leaves, not just modified ones. The counter half is recoverable from
`EntitySnapshot` (it captures `counters`), but the **Equipment/Aura-attached** half is not —
`EntitySnapshot` records a permanent's own `attachedTo`, not what was attached *to* the leaving
creature, so faithful "modified" on exit needs the snapshot extended to capture attachments at
battlefield-exit (or the predicate evaluated against pre-leave state). `add-feature` scope.

Blocked cards:
- **Costume Closet** [5] — `{1}{W}` Artifact; enters with two +1/+1 counters + sorcery-speed "{T}: move a counter to target creature you control" (both of those work today) + "Whenever a **modified** creature you control leaves the battlefield, put a +1/+1 counter on this artifact" (the blocked part)

## "Deals damage to a [filtered] creature" trigger (RecipientFilter.Matching on a deals-damage trigger)

> Whenever <this> deals damage to a **Spider**, destroy that creature.

A deals-damage trigger whose **recipient** is filtered to a creature matching a predicate
(`Triggers.dealsDamage(recipient = RecipientFilter.Matching(...))`) does not fire. In
`rules-engine/.../event/TriggerMatcher.kt`, `matchesDealsDamageTrigger` (the SELF-binding
deals-damage detection path via `DamageTriggerDetector`) has **no `is RecipientFilter.Matching`
case** — it falls through to `else -> false`, so the trigger never matches.
`RecipientFilter.Matching` is only wired for damage prevention/replacement/effect-targeting
(DamageCalculator, ReplacementEffectUtils, DamageUtils), never for trigger detection. This is a
pre-existing latent bug: the already-shipped **East-Mark Cavalier** (LTR) and **Mauhur,
Uruk-hai Captain** use the identical shape and are also silently broken. Fix (add-feature): add
`is RecipientFilter.Matching -> predicateEvaluator.matches(...)` (with LKI fallback like the
existing `CreatureYouControl` case) to `matchesDealsDamageTrigger`.

Blocked cards:
- **Spider-Slayer, Hatred Honed** [175] — `{2}` Legendary Artifact Creature; "Whenever Spider-Slayer deals damage to a Spider, destroy that creature" (blocked). Its other ability — `{6}`, exile-from-graveyard → two tapped 1/1 flying Robot tokens — works fine.

## Chosen card name surviving into a later-firing delayed trigger

> Choose a card name. Whenever a creature with the **chosen name** deals combat damage to a
> player this turn, draw a card.

`Effects.ChooseCardName` stores the name only in the resolving pipeline's `chosenValues`
(scoped to that one resolution). A `CardPredicate.NameEqualsChosen` in a **delayed** trigger's
event filter evaluates against `context.chosenValues`, which the later-firing delayed trigger
does not carry, so it fails closed and the trigger never fires. The only baker of chosen values
into delayed-trigger filters, `CreateDelayedTriggerExecutor.bakeChosenValuesIntoTrigger`,
handles only `HasSubtypeFromVariable` inside a `SpellCastEvent.spellFilter` — its own TODO
comment flags `NameEqualsChosen` as unhandled. Fix (add-feature): extend that baker to snapshot
`chosenValues` and rewrite `NameEqualsChosen` → literal `NameEquals(<name>)` inside delayed
event filters (e.g. `DealsDamageEvent.sourceFilter`), + verify the delayed matcher reads it.

Blocked cards:
- **The Clone Saga** [28] — `{3}{U}` Enchantment — Saga; chapters I (Surveil 3) and II (copy your next creature spell, non-legendary — both expressible today) are fine, but chapter III ("choose a card name … whenever a creature with the chosen name deals combat damage, draw") is blocked

## Exchange life totals with a player (CR 701.12c) + "life you lost this way" draw amount

> You may **exchange life totals** with target opponent. If you lost life this way, draw that
> many cards.

No player-vs-player life-total exchange exists. `LifeEffects` has only
`ExchangeLifeAndPowerEffect` (a player's life ↔ a *creature's power*, CR 701.12g) and
`ExchangeControlEffect` (control of permanents). Two sequential `SetLifeTotal`s can't reproduce
a *simultaneous* swap (the second read sees the already-mutated value), and there is no
life-total snapshot primitive. Separately, "draw that many cards" = the controller's life-loss
**delta from the exchange**, which no effect exposes as a `DynamicAmount`. Fix (add-feature): an
`ExchangeLifeTotalsEffect(target player)` executor honoring 701.12c (emitting gain/loss events
for lifelink/triggers) + a way to feed the controller's life-lost delta into `DrawCards`.

Blocked cards:
- **Mister Negative** [135] — `{5}{W}{B}` Vigilance/lifelink; "you may exchange life totals with target opponent. If you lost life this way, draw that many cards." (Vigilance + lifelink are fine; the ETB exchange is blocked.)

## "Different names" multi-target distinctness constraint

> Return up to six **target creature cards with different names** from your graveyard to the
> battlefield.

Cross-target selection constraints on `TargetObject` (enforced in `TargetValidator` /
`DecisionValidators`) currently cover only `sameController`, `sameOwner`, `sameCreatureType`,
and `totalManaValueAtMost` — plus object-identity distinctness (`TargetOther`, which prevents
picking the same entity twice, NOT the same *name*). There is no name-based distinctness gate,
so "with different names" can't be enforced. Fix (add-feature): a `differentNames` cross-target
requirement grouping chosen targets by projected card name, wired into both validators (+ SDK
reference + `CardLinter`).

Blocked cards:
- **Behold the Sinister Six!** [51] — `{6}{B}` Sorcery; "Return up to six target creature cards with different names from your graveyard to the battlefield." Dropping the constraint would wrongly allow six copies of the same-named creature, so it is not approximated.

## Color-filtered permanent "don't lose unspent [color] mana" static

> You don't lose unspent **red** mana as steps and phases end.

Needs a controller-scoped, single-color, **permanent** mana-retention static. The three existing
neighbors don't fit: `PreventManaPoolEmptying` (Upwelling) keeps *all* colors for *all* players;
`ConvertEmptyingManaToRed` (Ozai) *converts* other colors to red instead of losing them (so a
floating `{G}` wrongly becomes `{R}`); `RetainUnspentManaEffect` (The Last Agni Kai) is red-only
controller-only but a **one-shot turn-scoped** effect (cleared each cleanup), not a static tied
to a permanent's presence. Fix (add-feature): a color-parameterized `RetainUnspentColoredMana`
`StaticAbility` wired into `CleanupPhaseManager.emptyManaPools` + `CombatManager.endCombat` +
`StaticAbilityHandler`.

Blocked cards:
- **Electro, Assaulting Battery** [76] — `{1}{R}{R}` Flying; "You don't lose unspent red mana as steps and phases end." Its other clauses (Flying; cast-instant/sorcery → add {R}; LTB pay-{X} deal X to a player) are all expressible today.

## "Discard a card OR pay {2}" additional cost (DiscardOrPay)

> As an additional cost to cast this spell, **discard a card or pay {2}**.

A choice between a non-mana cost (discard a card) and a **mana** payment as an additional cost.
Not supported: `Costs.additional.Choice(...)` handles only non-mana options (`ChoiceCostResolver`
drops any `CostAtom.Mana` branch → the pay-{2} path silently disappears), and the `*OrPay`
family (`SacrificeOrPay`, `ExileFromGraveyardOrPay`, `BlightOrPay`, `BeholdOrPay`) has **no
`DiscardOrPay`**. The `ModalEffect` per-mode-cost workaround (Bitter Triumph "discard or pay 3
life") doesn't transfer because a mode's `CostAtom.Mana` additional cost is treated as a no-op
by `CastSpellHandler` (the {2} would be free). Fix (add-feature): a `DiscardOrPay(count, filter,
alternativeManaCost)` member of the `*OrPay` additional-cost family, wired into
`CastSpellEnumerator` + `CastSpellHandler`.

Blocked cards:
- **Pumpkin Bombardment** [139] — `{B/R}` Sorcery; "As an additional cost to cast this spell, discard a card or pay {2}. Deals 3 damage to target creature." (the damage half is trivial; the discard-or-pay additional cost is the blocker)

## "Play a land from anywhere other than your hand" trigger

> Whenever you **play a land** or cast a spell from anywhere other than your hand, …

The cast half ("cast a spell from a non-hand zone") is expressible (`SpellCastPredicate.CastFromZoneOtherThan`,
as Kellan the Kid uses). The **land-play** half is not: `PlayLandHandler` emits a plain
`ZoneChangeEvent(→ BATTLEFIELD)` with no "was played / special action" marker, indistinguishable
from a land an *effect* puts onto the battlefield (fetch, reanimate, ramp) — a `ZoneChangeEvent`
land trigger from a non-hand zone would over-trigger on every such put. (The Endstone dodges this
only by restricting to `from = HAND`, which Shadow can't use since it needs the non-hand zones;
`EventPattern.ZoneChangeEvent` also has no `excludeFrom` shape.) Fix (add-feature): a
`LandPlayedEvent` / `wasPlayed` marker from `PlayLandHandler` + a `Triggers.youPlayLand(fromZoneOtherThan=…)`
primitive mirroring the spell-cast one.

Blocked cards:
- **Shadow of the Goblin** [87] — `{1}{R}` Enchantment; first-main loot (fine) + "Whenever you play a land or cast a spell from anywhere other than your hand, deals 1 to each opponent" (the land-play-from-non-hand half is the blocker)

The **turn-scoped historical-condition** form of the same gap is also missing (distinct from the
trigger above): there is no `PlayerPlayedLandThisTurn(fromZone=…)` condition (land plays are
tracked only as a count via `LandDropsComponent` — no source-zone provenance, unlike
`CastSpellRecord.castFromZone` for spells), and no "cast a spell from **any zone other than
hand** this turn" condition (`YouCastSpellsThisTurn` is single-zone positive equality only; the
"other-than-hand" concept exists only at event/trigger level as `EventFilters.CastFromZoneOtherThan`).
Fix (add-feature): a land-play zone-of-origin turn record + an "other-than" zone qualifier on both
the land and spell turn-conditions.
- **Spider-Man 2099** [150] — `{U}{R}` double strike/vigilance; the "From the Future" turn-number cast restriction (`ControllerTurnsTakenAtMost`) and "deal power to any target" are fine, but the end-step intervening-if "if you've played a land or cast a spell this turn from anywhere other than your hand" is the blocker.

## Temporary "play from top of library, paying life = mana value instead of mana cost"

> Whenever Gwenom attacks, until end of turn, you may look at the top card of your library any
> time and you may play cards from the top. If you cast a spell this way, **pay life equal to its
> mana value rather than pay its mana cost**.

Two independent gaps: (1) The "pay life = mana value instead of mana cost" hook
(`AdditionalCost.PayLifeEqualToManaValueOfSpell`) is wired **only** on the linked-exile play
permission (Valgavoth), not on the top-of-library statics (`PlayFromTopOfLibrary` etc. carry no
`additionalCost`/`withoutPayingManaCost`; `CastFromZoneEnumerator.enumerateTopOfLibrary` +
`CastSpellHandler` always compute the normal mana cost). (2) The ~5 play-from-top / look-at-top
read helpers scan only *printed* `staticAbilities`, never `state.grantedStaticAbilities`, so an
until-end-of-turn floating grant of the permission does nothing (unlike the graveyard-play path,
which already reads the floating channel). Fix (add-feature): add an alternative-cost hook to a
top-of-library permission + wire the enumerator/handler to it, and extend the top-of-library read
sites to consult granted statics (mirroring `MayCastFromGraveyard`).

Blocked cards:
- **Gwenom, Remorseless** [56] — `{3}{B}{B}` Deathtouch/lifelink; the attack-granted "play from top, pay life = mana value" is the blocker (deathtouch, lifelink, and the attack trigger itself are fine).

## "Prevent damage to this creature, put that many +1/+1 counters on it" self-replacement

> If damage would be dealt to Anti-Venom, prevent that damage and put that many +1/+1 counters
> on him.

The SDK replacement `ReplaceDamageWithCounters` + `RecipientFilter.Self` exists, but the engine
executor (`DamageUtils.applyReplaceDamageWithCounters`) is wired **only** on the player-damage
path (its two call sites are both `isPlayer`-guarded / inside `applyDamageToPlayer`), and its
recipient matcher handles only `RecipientFilter.You`/`.Any` (no `Self` case). The creature-damage
path never invokes it, so damage to a creature is never replaced. The only shipped user is Force
Bubble (damage to *you*). Note: Strength of Will's indestructible + `TakesDamage`-trigger counters
is NOT a faithful substitute — that *marks* the damage (survives only via indestructible) rather
than preventing/replacing it. Fix (add-feature): accept `RecipientFilter.Self` (targetId == host)
and invoke `applyReplaceDamageWithCounters` on the creature-damage paths
(`CombatDamageManager.applyDamageToCreature` + the non-player branch of `DamageUtils.applyDamage`).
(The "if he was cast" ETB reanimation half is fine — `Conditions`/`WasCast` exists.)

Blocked cards:
- **Anti-Venom, Horrifying Healer** [1] — `{W}{W}{W}{W}{W}` Symbiote Hero; ETB "if cast, reanimate a creature" is fine, but the damage-prevention-to-counters self-replacement is the blocker.

## Granted activated ability with `UntilYourNextTurn` duration never expires

> …until your next turn, he becomes a land and **gains "{T}: Add {U}."**

Granting an *activated* ability routes only through `GameState.grantedActivatedAbilities`, which
has no floating-effect/projection path and is pruned in only three places — none handling
`Duration.UntilYourNextTurn` (`CleanupPhaseManager` end-of-turn only;
`EndedDurationExpiryCheck` counter/tapped only; `ZoneTransitionService` on-leave only). Notably
`CleanupPhaseManager.expireUntilYourNextTurnEffects` prunes floating effects and
`globalGrantedTriggeredAbilities` for `UntilYourNextTurn` but **not** `grantedActivatedAbilities`
(nor the sibling granted-triggered/static/replacement/keyword lists). Symptom: after Hydro-Man
reverts to a creature next turn, the "{T}: Add {U}" grant persists — a permanent 2/2 that taps
for blue forever. The type-change and untap halves work; only the granted activated ability
leaks. Fix (add-feature): extend `expireUntilYourNextTurnEffects` to drop
`grantedActivatedAbilities` (and siblings) with `UntilYourNextTurn` duration, keyed to the
grant-holder's controller (the `GrantedActivatedAbility` record needs a controller/expires-for
field, like the player-component grants).

Blocked cards:
- **Hydro-Man, Fluid Felon** [33] — `{U}{U}`; blue-cast pump (fine) + end-step "untap; until your next turn becomes a non-creature land with '{T}: Add {U}'" — the type-change + untap work, but the granted mana ability never expires.

## Static damage redirect to the enchanted/equipped creature (Pariah-style)

> All damage that would be dealt to you is dealt to **enchanted creature** instead.

The static-redirect resolver `DamageUtils.resolveRedirectTarget` is a bespoke resolver (it does
NOT delegate to the general `TargetResolutionUtils.resolveTarget`) and handles only
`ControllerOfDamageSource`, `Controller`, `TargetController`, `Self` — `EffectTarget.EnchantedCreature`
(and `EquippedCreature`/`EnchantedPermanent`) hits `else -> null`, so the redirect is silently
skipped and damage stays on the player. All three shipped static-`RedirectDamage` cards
(Ancient Adamantoise, Martyrs of Korlis, Harsh Judgment) redirect to `Self`; no Aura→enchanted
(Pariah-style) redirect exists. The "+2/+2 for each Aura/Equipment attached" buff clause IS
expressible (`GrantDynamicStatsEffect` over `attachedCreature()` with
`AttachmentCount`). Fix (add-feature): extend `resolveRedirectTarget` with
`EnchantedCreature`/`EquippedCreature`/`EnchantedPermanent` → the Aura's `AttachedToComponent.targetId`.

Blocked cards:
- **With Great Power . . .** [24] — `{3}{W}` Aura; "+2/+2 per attached Aura/Equipment" (fine) + "all damage that would be dealt to you is dealt to enchanted creature instead" (the redirect is the blocker).

## "The legend rule doesn't apply to [filter]" exemption

> The "legend rule" doesn't apply to **Spiders you control**.

No SDK static or effect exempts a filtered group from the legend rule. `LegendRuleCheck`
(`rules-engine/.../mechanics/sba/permanent/LegendRuleCheck.kt`) is a hard-coded state-based
action with no exemption/filter hook. Fix (add-feature): a `LegendRuleDoesNotApplyTo(filter)`
static + a consult hook in `LegendRuleCheck.check`. (The card's other clause — "whenever you cast
a spell from a non-hand zone, you may copy it once per turn; permanent copy gains haste" — is
fully expressible via `youCastSpell(CastFromZoneOtherThan(HAND))` + `oncePerTurn` +
`CopyTargetSpell(addedTokenKeywords = HASTE)`.)

Blocked cards:
- **Spider-Verse** [93] — `{3}{R}{R}` Enchantment; the legend-rule exemption for Spiders is the blocker (the copy-spell-from-non-hand clause is fine).

## Play cards exiled **face down** from an opponent's library (controller may look + cast)

> Look at the top nine cards of target opponent's library, **exile two of them face down**, then
> put the rest on the bottom in a random order. **You may play the exiled cards** for as long as
> they remain exiled. Mana of any type can be spent to cast spells this way.

`FaceDownMode.HIDDEN` is defined as face down with **no turn-up procedure** — "simply hidden;
nothing lets it be turned face up in place" (used for Hideaway, where the card is later played via
a dedicated activated ability that re-gathers `FromLinkedExile()` and grants may-play +
without-paying-cost at activation time — e.g. Clive's Hideaway, Mosswort Bridge). Black Cat instead
grants a **persistent** `GrantMayPlayFromExileEffect(MayPlayExpiry.Permanent)` directly over cards
sitting in exile `HIDDEN`, expecting the controller to see and freely cast them from the exile zone
at any time. That path isn't wired: HIDDEN cards are masked from everyone (including the
controller), and the playable-action computation does not surface a face-down exiled card as a
castable option. There is no face-down mode meaning "hidden from opponents, but the controller may
look at and cast it from exile." Fix (add-feature): a controller-visible face-down-in-exile mode +
masking that reveals those cards to their controller only + the cast-from-exile / legal-action path
recognizing persistent-may-play over face-down exiled cards (cross-layer: masking → ClientDTO →
CastSpellHandler).

Blocked cards:
- **Black Cat, Cunning Thief** [52] — `{3}{B}{B}` Legendary Creature — Human Rogue Villain, 2/3; the ETB look/exile-two-face-down/bottom-rest pipeline resolves, but "you may play the exiled cards" is uncastable because the face-down HIDDEN exiled cards never surface as playable to the controller. (Previously authored on branch `spm-no-engine`, then removed pending this feature.)

---

## Known divergences (card IS implemented, but one clause needs a future engine feature)

- **Superior Foes of Spider-Man** [96] — *implemented and committed.* "you may play that card
  **until you exile another card with this creature**" is modeled with `MayPlayExpiry.Permanent`
  (play for as long as it stays exiled). Faithful in normal play; the only divergence is the
  rare case where a second mv≥4 spell is cast while a still-unplayed card from a prior trigger
  sits in exile — the engine keeps both playable instead of revoking the earlier one. Strict
  fidelity would need a new source-scoped "supersede prior grant" `MayPlayExpiry` variant
  (add-feature).

---

<!-- Additional mechanics appended below as the loop encounters them. -->
