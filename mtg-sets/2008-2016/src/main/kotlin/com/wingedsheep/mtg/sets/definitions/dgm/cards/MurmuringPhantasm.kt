package com.wingedsheep.mtg.sets.definitions.dgm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Murmuring Phantasm
 * {1}{U}
 * Creature — Spirit
 * 0/5
 *
 * Defender
 */
val MurmuringPhantasm = card("Murmuring Phantasm") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    oracleText = "Defender"
    power = 0
    toughness = 5

    keywords(Keyword.DEFENDER)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Peter Mohrbacher"
        flavorText = "\"The most insidious thing in the world is nonsense that sounds just plausible enough to listen to.\"\n—Lazav"
        imageUri = "https://cards.scryfall.io/normal/front/9/7/9752644c-7c43-429e-a79c-1239b9a0bc8a.jpg?1783940041"
    }
}
