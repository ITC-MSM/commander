package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wrecking Crew
 * {4}{R}
 * Creature — Human Warrior
 * 4 / 5
 * Reach (This creature can block creatures with flying.)
 * Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)
 */
val WreckingCrew = card("Wrecking Crew") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    oracleText = "Reach (This creature can block creatures with flying.)\nTrample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)"
    power = 4
    toughness = 5

    keywords(Keyword.REACH, Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "132"
        artist = "Joshua Raphael"
        flavorText = "They built the neighborhood. They know its weak points. They know all the hiding places. When they come for you, they'll find you and bring the roof down on your head."
        imageUri = "https://cards.scryfall.io/normal/front/9/2/92691507-b1ce-40d0-87e7-b79e81370511.jpg?1783923111"
    }
}
