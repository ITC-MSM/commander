package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * April, Reporter of the Weird
 * {2}{U}
 * Legendary Creature — Human Detective
 * 2/2
 *
 * Whenever April deals combat damage to a player, draw that many cards,
 * then discard a card.
 */
val AprilReporterOfTheWeird = card("April, Reporter of the Weird") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Detective"
    oracleText = "Whenever April deals combat damage to a player, draw that many cards, then discard a card."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.Composite(
            listOf(
                Effects.DrawCards(DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT)),
                Effects.Discard(1)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "30"
        artist = "Pauline Voss"
        flavorText = "\"Freelance reporting pays even worse than tutoring, but the satisfaction of uncovering the truth makes it all worth it! Also the free ringside tickets to cover intergalactic wrestling don't suck.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31aa943f-c9db-43dc-8a72-7ef56f9f5c8b.jpg?1771502549"
    }
}
