package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stomper Cub
 * {3}{G}{G}
 * Creature — Beast
 * 5 / 3
 *
 * Trample
 *
 * Modeling notes:
 *  - Vanilla trampler; a single `keywords(Keyword.TRAMPLE)` declaration covers the printed line.
 */
val StomperCub = card("Stomper Cub") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 3
    oracleText = "Trample"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "210"
        artist = "Karl Kopinski"
        flavorText = "\"This one is only a yearling, but it would be wise to move away before we provide it with excellent hunting practice.\"\n—Samila, Murasa Expeditionary House"
        imageUri = "https://cards.scryfall.io/normal/front/8/9/89be64a8-dd78-48c3-bb47-4f2a5ad9ec10.jpg?1783941959"
    }
}
