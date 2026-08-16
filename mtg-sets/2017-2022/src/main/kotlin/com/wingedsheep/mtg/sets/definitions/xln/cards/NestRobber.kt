package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nest Robber
 * {1}{R}
 * Creature — Dinosaur
 * 2/1
 * Haste
 */
val NestRobber = card("Nest Robber") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    power = 2
    toughness = 1
    oracleText = "Haste"

    keywords(Keyword.HASTE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Jonathan Kuo"
        flavorText = "\"These sailors on our shores are like the dinosaurs that plunder eggs from nests. They live on the labors of others.\" —Itzama the Crested"
        imageUri = "https://cards.scryfall.io/normal/front/5/7/576d3845-f45a-4db0-9f7c-845cedb64c49.jpg?1783935742"
    }
}
