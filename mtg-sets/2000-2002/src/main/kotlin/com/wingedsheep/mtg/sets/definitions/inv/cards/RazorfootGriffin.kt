package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Razorfoot Griffin
 * {3}{W}
 * Creature — Griffin
 * 2/2
 * Flying
 * First strike
 */
val RazorfootGriffin = card("Razorfoot Griffin") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    power = 2
    toughness = 2
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
        "First strike (This creature deals combat damage before creatures without first strike.)"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Ben Thompson"
        imageUri = "https://cards.scryfall.io/normal/front/8/1/819e2046-9b78-4fd0-92f8-798bfac51195.jpg?1562921137"
    }
}
