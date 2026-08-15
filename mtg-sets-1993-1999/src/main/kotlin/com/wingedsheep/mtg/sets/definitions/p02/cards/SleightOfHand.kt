package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sleight of Hand
 * {U}
 * Sorcery
 * Look at the top two cards of your library. Put one of them into your hand and the other on
 * the bottom of your library.
 *
 * Canonical printing lives in Portal Second Age (P02), the card's earliest real printing.
 * Later reprints (e.g. Wilds of Eldraine) contribute only a [com.wingedsheep.sdk.model.Printing] row.
 *
 * Modeled with the standard dig recipe [Patterns.Library.lookAtTopAndKeep]: gather the top two,
 * keep one in hand, the single remaining card goes to the bottom of the library.
 */
val SleightOfHand = card("Sleight of Hand") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Look at the top two cards of your library. Put one of them into your hand and the " +
        "other on the bottom of your library."

    spell {
        effect = Patterns.Library.lookAtTopAndKeep(
            count = DynamicAmount.Fixed(2),
            keepCount = DynamicAmount.Fixed(1),
            keepDestination = CardDestination.ToZone(Zone.HAND),
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Phil Foglio"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3405184-dcda-4bb6-ade6-c2a87bc3296d.jpg?1783946484"
    }
}
