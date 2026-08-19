package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Woodland Changeling
 * {1}{G}
 * Creature — Shapeshifter
 * 2/2
 * Changeling (This card is every creature type.)
 */
val WoodlandChangeling = card("Woodland Changeling") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Shapeshifter"
    power = 2
    toughness = 2
    oracleText = "Changeling (This card is every creature type.)"

    keywords(Keyword.CHANGELING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "242"
        artist = "Franz Vohwinkel"
        flavorText = "Changelings cannot resist the draw of a new shape, even if doing so would be in their best interests."
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa41edf8-88a1-46dd-8315-b0afe7e14b7e.jpg?1783942855"
    }
}
