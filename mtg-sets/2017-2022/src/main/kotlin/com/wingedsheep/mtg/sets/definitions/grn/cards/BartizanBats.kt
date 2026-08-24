package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bartizan Bats
 * {3}{B}
 * Creature — Bat
 * 3/1
 * Flying (This creature can't be blocked except by creatures with flying or reach.)
 */
val BartizanBats = card("Bartizan Bats") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Bat"
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)"
    power = 3
    toughness = 1

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "62"
        artist = "Nils Hamm"
        flavorText = "\"Bats are welcome to eat thousands of my pets. I have multitudes more that will ultimately eat the bats.\"\n—Izoni"
        imageUri = "https://cards.scryfall.io/normal/front/2/1/210da4ad-d8c5-436b-b1a4-5233e8074a1b.jpg?1783934180"
    }
}
