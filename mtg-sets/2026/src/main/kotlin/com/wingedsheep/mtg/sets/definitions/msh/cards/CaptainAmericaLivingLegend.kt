package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Captain America, Living Legend
 * {1}{W}{U}
 * Legendary Creature — Human Soldier Hero
 * 3/4
 * Vigilance
 * Whenever a creature you control becomes tapped during your turn, if it's the first time that
 * creature has become tapped this turn, untap it.
 *
 * Three riders on one "becomes tapped" observer, each carried by an existing axis:
 *
 * - **"a creature you control"** — the `ANY` binding plus `GameObjectFilter.Creature.youControl()`.
 *   Captain America is himself a creature you control, so the ANY binding covers the self case too.
 * - **"during your turn"** — `triggerRestriction = Conditions.IsYourTurn`. It is an adverbial
 *   narrowing of the *trigger event*, not the printed "if", so it is checked when the trigger would
 *   fire and never again (CR 603.2). Tapping your creatures on an opponent's turn does nothing.
 * - **"if it's the first time that creature has become tapped this turn"** —
 *   `firstTimeEachTurn = true`, the per-**permanent** window
 *   (`EventPattern.TapEvent.firstTimeEachTurn`). Note this is *not* `oncePerTurn`, which caps the
 *   *ability* at one firing per turn and would therefore untap only the first of several creatures
 *   tapped that turn. Tap three creatures for convoke and all three untap; untap one by other means
 *   and tap it again the same turn and it stays tapped.
 *
 * A creature that *entered the battlefield tapped* never became tapped (CR 701.26a — only untapped
 * permanents can be tapped), so it emits no tap event on entry and its first real tap that turn still
 * gets the untap.
 *
 * Untapping emits an `UntappedEvent`, never a `TappedEvent`, so the ability cannot loop; and because
 * the window is per-permanent, re-tapping the untapped creature that turn does not fire it again.
 * Untapping an attacking creature does not remove it from combat (CR 506.4b — "tapping or untapping a
 * creature that's already been declared as an attacker or blocker doesn't remove it from combat and
 * doesn't prevent its combat damage"), which is why the convoke/crew/tap-for-mana angle and the attack
 * angle both work.
 *
 * **Known divergence in the "if" clause's timing.** The clause that immediately follows the trigger
 * event — "if it's the first time that creature has become tapped this turn" — is the printed
 * intervening-`if` (CR 603.4), so it should be checked on trigger *and* re-checked on resolution.
 * Here it is carried by the `firstTimeEachTurn` event-pattern rider, which is evaluated only when the
 * tap event happens, so the second check never runs. It is deliberately **not** [interveningIf]:
 * that field takes a `Condition` evaluated against game state at resolution, and the fact this clause
 * needs — the triggering `TappedEvent.firstThisTurn` — is a property of the event, which the stack
 * object does not carry (it carries last-known subtypes and card types, not event flags).
 *
 * The "during your turn" half is *not* part of this: it narrows the trigger event, so being checked
 * once is correct for it, and it is a `triggerRestriction`.
 *
 * The divergence needs the creature untapped and tapped again in response to the trigger — two
 * instants deep — and can only grant an extra untap, never withhold one.
 */
val CaptainAmericaLivingLegend = card("Captain America, Living Legend") {
    manaCost = "{1}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Soldier Hero"
    oracleText = "Vigilance\n" +
        "Whenever a creature you control becomes tapped during your turn, if it's the first time " +
        "that creature has become tapped this turn, untap it."
    power = 3
    toughness = 4
    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.becomesTapped(
            binding = TriggerBinding.ANY,
            filter = GameObjectFilter.Creature.youControl(),
            firstTimeEachTurn = true
        )
        triggerRestriction = Conditions.IsYourTurn
        effect = Effects.Untap(EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "210"
        artist = "Smirtouille"
        flavorText = "\"They say the world moved on without me, but as long as injustice exists, " +
            "I'm still needed here.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/5/6516f292-469d-4092-b099-97c698f373cd.jpg?1783902903"
    }
}
