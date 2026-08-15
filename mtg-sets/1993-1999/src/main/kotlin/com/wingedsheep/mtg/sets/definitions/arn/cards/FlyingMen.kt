package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flying Men
 * {U}
 * Creature — Human
 * 1/1
 * Flying
 */
val FlyingMen = card("Flying Men") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human"
    power = 1
    toughness = 1
    oracleText = "Flying"
    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Christopher Rush"
        flavorText = "Saffiyah clapped her hands and twenty flying men appeared at her side, each well trained in the art of combat."
        imageUri = "https://cards.scryfall.io/normal/front/2/5/25ab9a2b-e248-4ae2-aac3-b49fdb3e260a.jpg?1562902022"
    }
}
