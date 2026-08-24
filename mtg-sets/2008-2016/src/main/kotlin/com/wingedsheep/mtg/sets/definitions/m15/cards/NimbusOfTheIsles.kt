package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nimbus of the Isles
 * {4}{U}
 * Creature — Elemental
 * 3/3
 * Flying
 */
val NimbusOfTheIsles = card("Nimbus of the Isles") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 3
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Cliff Childs"
        flavorText = "The people of the Sevick Isles have a unique understanding of the term \"ominous clouds.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d239c66-3e3f-4dc4-bede-f264864583b1.jpg?1783939190"
    }
}
