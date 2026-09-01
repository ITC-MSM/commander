package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunspire Griffin
 * {1}{W}{W}
 * Creature — Griffin
 * 2/3
 *
 * Flying
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * One evergreen keyword and nothing else.
 */
val SunspireGriffin = card("Sunspire Griffin") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    oracleText = "Flying"
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "Johannes Voss"
        flavorText = "\"For each griffin wounded by an arrow, there's a corpse with a bow nearby.\"\n" +
            "—Pel Javya, Wojek investigator"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/1388ce6e-8199-46c1-8ee3-71266b0929bf.jpg?1783940373"
    }
}
