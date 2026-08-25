package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wall of Earth
 * {1}{R}
 * Creature — Wall
 * 0/6
 *
 * Defender (This creature can't attack.)
 */
val WallOfEarth = card("Wall of Earth") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wall"
    power = 0
    toughness = 6
    oracleText = "Defender (This creature can't attack.)"

    keywords(Keyword.DEFENDER)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Richard Thomas"
        flavorText = "The ground shuddered violently and the earth seemed to come to life. The elemental force " +
            "contained in the vast wall of earth was trapped, bent to its controller's will."
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c12e97c1-ca28-432a-8140-3f08bb4485a3.jpg?1783948051"
    }
}
