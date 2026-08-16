package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Roc Hunter
 * {1}{R}
 * Creature — Human Soldier
 * 3/1
 * Reach
 */
val RocHunter = card("Roc Hunter") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 1
    oracleText = "Reach"

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "Ina Wong"
        flavorText = "\"Never track the roc itself with your eyes, for it will swoop with the sun at its back and blind you. Instead, track its shadow and, when it's almost upon you, heave your javelin upward with all your might.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c8b2f2b-6f19-47f7-bb65-00665989bc30.jpg?1783920060"
    }
}
