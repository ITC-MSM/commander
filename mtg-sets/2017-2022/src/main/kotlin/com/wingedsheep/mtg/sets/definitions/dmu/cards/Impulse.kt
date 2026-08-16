package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

/**
 * Impulse
 * {1}{U}
 * Instant
 * Look at the top four cards of your library. Put one of them into your hand and the rest on the bottom of your library in any order.
 */
val Impulse = card("Impulse") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Look at the top four cards of your library. Put one of them into your hand and the rest on the bottom of your library in any order."

    spell {
        effect = Patterns.Library.lookAtTopAndKeep(
            count = 4,
            keepCount = 1,
            keepDestination = CardDestination.ToZone(Zone.HAND),
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
            restOrder = CardOrder.ControllerChooses
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "55"
        artist = "Sam Guay"
        flavorText = "\"I've always made snap decisions. I'm just better at making the right ones now that I'm older and wiser.\"\n—Teferi"
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5aec2b2c-0764-4869-814d-aad921122af9.jpg?1673306762"
    }
}
