package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Terrasymbiosis
 * {2}{G}
 * Enchantment
 * Whenever you put one or more +1/+1 counters on a creature you control, you may draw
 * that many cards. Do this only once each turn.
 *
 * Implementation: a `CountersPlacedEvent` for `Counters.PLUS_ONE_PLUS_ONE` filtered to
 * creatures you control and scoped to `placedBy = Player.You` (the printed "**you** put"),
 * which exposes the placed count via `TRIGGER_COUNTERS_PLACED_AMOUNT`. The "may"
 * is a `MayEffect` wrapping the draw — a bare `optional = true` flag on a no-target
 * triggered ability is a silent no-op (the engine only honours it on targeted abilities
 * or ones with an `elseEffect`), so the player would never have been prompted.
 *
 * "Do this only once each turn" is `effectOncePerTurn = true` — CR 603.2h, the rider keyed to the
 * *action*: the ability keeps triggering until the draw is actually taken, so declining one
 * placement leaves a later, bigger one still on offer. Not `oncePerTurn` (Scavenger's Talent's
 * "**this ability triggers** only once each turn"), which the first declined trigger would burn.
 */
val Terrasymbiosis = card("Terrasymbiosis") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Whenever you put one or more +1/+1 counters on a creature you control, " +
        "you may draw that many cards. Do this only once each turn."

    triggeredAbility {
        // "Whenever **you** put …" — CR 122.6 makes the placer part of the event, so this needs
        // `placedBy`; the shared `PlusOneCountersPlacedOnYourCreature` leaves it null, which is the
        // passive "are put on a creature you control" wording and fires on an opponent's placement
        // too. Same fix as Stocking the Pantry, which the differential caught.
        trigger = Triggers.countersPlacedOn(
            filter = GameObjectFilter.Creature.youControl(),
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            firstTimeEachTurn = false,
            placedBy = Player.You,
        )
        effectOncePerTurn = true
        effect = MayEffect(
            Effects.DrawCards(
                DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_COUNTERS_PLACED_AMOUNT)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "210"
        artist = "Viko Menezes"
        flavorText = "For Eumidians, terraforming and evolution are one and the same. They grow as their planet grows, in lockstep coexistence."
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26008c7d-5dbe-4da2-b475-4dd307e7bc68.jpg?1752947411"
    }
}
