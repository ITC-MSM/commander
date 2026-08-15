package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pond Prophet
 * {G/U}{G/U}
 * Creature — Frog Advisor
 * 1/1
 *
 * When this creature enters, draw a card.
 */
val PondProphet = card("Pond Prophet") {
    manaCost = "{G/U}{G/U}"
    colorIdentity = "UG"
    typeLine = "Creature — Frog Advisor"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "229"
        artist = "Simon Dominic"
        flavorText = "\"The rainfall brings us news of the world around us. I've learned a lot, but most of all I've learned that it's very exciting out there!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb959e74-61ea-453d-bb9f-ad0183c0e1b1.jpg?1721427169"
    }
}
