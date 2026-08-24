package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wall of Mist
 * {1}{U}
 * Creature — Wall
 * 0/5
 * Defender
 */
val WallOfMist = card("Wall of Mist") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Wall"
    oracleText = "Defender"
    power = 0
    toughness = 5

    keywords(Keyword.DEFENDER)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "83"
        artist = "Dimitar Marinski"
        flavorText = "The seafloor is flecked with the bones of fools who dared to sail into the mist."
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fb995c8-1bc2-4ff4-b8e9-f9b6bc0de0fe.jpg?1783934577"
    }
}
