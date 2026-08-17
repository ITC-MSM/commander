# Marvel Super Heroes (MSH) — engine gaps and known limitations

Recorded 2026-08-17 by the `verify-set` pass that closed this backlog. Every MSH card exists, matches
Scryfall field for field, and had its `cardDef { }` read against its oracle text. These are the things
that pass surfaced and **deliberately did not fix**, because each is engine or SDK work rather than card
authoring (`add-feature` territory, per `AGENTS.md`). A gap here is a documented decision, not an excuse.

Ordered by how much it matters.

## 1. `DamageType` is silently unenforced on `ModifyDamageAmount` — affects Hawkeye, Young Avenger [MSH]

`rules-engine/src/main/kotlin/com/wingedsheep/engine/handlers/effects/DamageUtils.kt`, the
`ModifyDamageAmount` loop (~line 1863). It checks `restrictions`, `damageSourceMatches` and
`damageRecipientMatches`, but never `damageEvent.damageType`. The `DoubleDamage` loop ~60 lines above it
(~1805-1817) *does*, with a six-line `damageTypeMatches` when-block, and combat damage reaches both
through the same `applyStaticDamageAmplification(isCombatDamage = true)`.

**Effect:** Hawkeye's "+X to *noncombat* damage" also amplifies your creatures' combat damage.

**Why it matters here specifically:** four cards in the whole corpus use `ModifyDamageAmount`, and
Hawkeye, Young Avenger is the **only** one that pairs it with a non-`Any` `DamageType`. This latent bug
is exposed by exactly one card, and that card is in this set.

**Fix:** copy the `damageTypeMatches` guard into the `ModifyDamageAmount` branch. Engine change, so its
own PR + `just test-rules`, plus a `HawkeyeYoungAvengerScenarioTest` asserting a burn spell is amplified
and a creature's combat damage is not.

## 2. Resolution-time snapshot where CR 611.2c requires a dynamic set — The Kingpin of Crime [MSH]

> Whenever you attack, you may pay 2 life. If you do, until end of turn, creatures you control with
> toughness greater than their power assign combat damage equal to their toughness rather than their power.

Implemented as a snapshot at resolution (`Effects.ForEachInGroup` granting each matching creature the
`ASSIGNS_COMBAT_DAMAGE_AS_TOUGHNESS` flag until end of turn).

**CR 611.2c**: a continuous effect from a resolving spell or ability that does *not* modify
characteristics or change controller "modifies the rules of the game, so it can affect objects that
weren't affected when that continuous effect began" — its own example is a damage-prevention effect
reaching creatures that were not on the battlefield yet. Assigning damage as toughness changes no
characteristic, so the affected set must stay **dynamic**.

**Observable:** attack, pay 2 life, then raise a creature's toughness above its power before the damage
step. The printed card includes it; ours does not.

**Fix:** needs a floating filtered continuous effect — there is no group-dynamic keyword grant in
`Effects.*`. The card's KDoc has been corrected to state this as a known limitation rather than as
correct timing.

## 3. Aggregate mana-value caps ignore battlefield permanents — The Super Hero Civil War [MSH]

`TargetValidator` sums only *card* targets, so "up to two target creatures with total mana value 6 or
less" is authoritatively unchecked for permanents (they contribute 0). The enumerator is therefore
over-permissive. Pre-existing and shared with Fall of Gil-galad and Fire Lord Sozin — not introduced here.

## 4. `YouAttackEvent` has no defender narrowing — Attuma, Atlantean Warlord [MSH]

"Whenever one or more Merfolk you control attack **a player**" also fires when the attack is aimed at a
planeswalker or a battle. Shared over-broad idiom with Meriadoc Brandybuck, Landroval and Horn of the
Mark; fixing it should converge all four.

## 5. Attach-on-attack/block reads the wrong controller — Super-Soldier Serum [MSH]

The attach ability is granted to the enchanted creature, so "Equipment you control" resolves against the
*creature's* controller rather than the Aura's. Forced: `AttachmentTriggerDetector` has no block branch.
A `BlockEvent` branch would remove the deviation.

## 6. Sibling target choices are invisible to enumeration — Cloak and Dagger, Entwined [MSH]

`TargetFinder.findLegalTargets` has no view of other target slots, so the faithful
"a creature *that player* controls" enumerates empty. The card uses "any opponent's creature" instead —
identical in two-player, laxer in a pod.

## 7. `evaluateDynamicCap` reads base power instead of last-known information

A dynamic power cap reads the source's base power after it has left the battlefield rather than LKI
(CR 608.2h / 113.7a). Affects Loki, Laufeyson's rider. Carried over from the `msh-rest` loop ledger.

## 8. As-enters counters with a dynamic count — Vision Quest [MSH]

"with X +1/+1 counters on it" is implemented as a `MoveCollectionEffect` followed by a separate
`AddCountersToCollectionEffect`, so the creature does not enter *with* them. The as-enters idiom
(`addCounterType`, CR 614.1c) takes a fixed counter type, and here the count is **X** — not expressible
with current vocabulary. Impact is narrow: state-based actions do not run mid-resolution, so a 0/0 does
not die; only an ETB trigger on the fetched creature reading its own counters sees pre-counter values.
(The fixed-count instances of this same shape — Thunderbolts Conspiracy — *were* fixed.)

## 9. Sequential rather than simultaneous per-player puts — Worlds Within Worlds [MSH]

Each player's put happens immediately after their own choice instead of all puts happening after all
choices, so a later player in APNAP order sees earlier players' creatures already on the battlefield.
Needs a choose-pass / move-pass split.

## 10. Zone-agnostic gathers

A gather has no way to restrict its source zone, so a card moved out of the graveyard in response is
still exiled (Moonstone, Harsh Mistress — self-documented in the card's KDoc). The same shape appears in
blink-return effects that omit `fromZone = Zone.EXILE` (S.H.I.E.L.D. Flying Car, and the shared
`AbueloAncestralEcho` idiom).

## 11. Modal announce timing on a top-level trigger effect

A top-level `ModalEffect` on a triggered ability announces its mode as the ability goes on the stack
(CR 603.3c), whereas a non-modal printed "or" clause should be chosen on resolution. MSH's Ant-Man's Army
and Giant-Sized Flying Ant use the top-level shape; the corpus has both precedents (Wingnut, Bat on the
Belfry top-level vs Sewer Veillance Cam nested). Needs one corpus-wide ruling, not an MSH edit.

---

## Small SDK additions this pass wanted

- `Subtype.SYNTHEZOID` — absent. No MSH booster card needs it (Viv Vision is `Robot Hero`), but
  Vision, Synthezoid Avenger will.
- A `notAttacking()` builder on `ObjectFilter`/`TargetFilter`. Spider-Man, To the Rescue hand-rolls
  `StatePredicate.Not(StatePredicate.IsAttacking)`; `mir/cards/Alarum.kt` silently drops the restriction.
- `Keyword.EXTORT` — absent. The Kingpin of Crime composes it as
  `Triggers.YouCastSpell` + `MayPayManaEffect("{W/B}", DrainLife(1))`. Promote it when a second extort
  card lands.
- `Targets.ArtifactOrEnchantment` has no noncreature variant, so Guerrilla Gorilla hand-rolls its target.
- No `Patterns.Hand` composition for reveal-N-choose-one-discard; Klaw, Sonic Subjugator is the third
  hand-rolled copy (after Blackmail and Cabal Interrogator).
