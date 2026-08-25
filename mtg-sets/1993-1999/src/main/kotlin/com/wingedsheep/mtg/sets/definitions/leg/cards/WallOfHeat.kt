package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wall of Heat
 * {2}{R}
 * Creature — Wall
 * 2/6
 *
 * Defender (This creature can't attack.)
 */
val WallOfHeat = card("Wall of Heat") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wall"
    power = 2
    toughness = 6
    oracleText = "Defender (This creature can't attack.)"

    keywords(Keyword.DEFENDER)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Richard Thomas"
        flavorText = "At a distance, we mistook the sound for a waterfall . . ."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a38059a8-be69-4cc1-969b-951c610f2f11.jpg?1783948051"
    }
}
