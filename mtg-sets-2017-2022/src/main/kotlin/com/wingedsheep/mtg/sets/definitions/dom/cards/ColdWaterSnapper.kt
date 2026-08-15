package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cold-Water Snapper
 * {5}{U}
 * Creature — Turtle
 * 4/5
 * Hexproof
 */
val ColdWaterSnapper = card("Cold-Water Snapper") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Turtle"
    power = 4
    toughness = 5
    oracleText = "Hexproof"

    keywords(Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Jason Kang"
        flavorText = "\"For generations, the inhabitants of Orvada have hungered for turtle soup. For generations, the turtles have frustrated their ambitions.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cf339549-4325-40c6-adde-0cd31bb738e0.jpg?1562743213"
    }
}
