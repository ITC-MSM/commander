package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lowland Giant
 * {2}{R}{R}
 * Creature — Giant
 * 4/3
 *
 * Vanilla — no rules text.
 */
val LowlandGiant = card("Lowland Giant") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant"
    power = 4
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "187"
        artist = "Paolo Parente"
        flavorText = "\"Faugh!\" snorted Tahngarth. \"Why would it make a meal of something like you?\" Squee looked relieved. \"No,\" he continued, \"you'd make a much better toothpick.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/3/7398dec7-5e60-43c0-81a0-ab49beb37077.jpg?1783946627"
    }
}
