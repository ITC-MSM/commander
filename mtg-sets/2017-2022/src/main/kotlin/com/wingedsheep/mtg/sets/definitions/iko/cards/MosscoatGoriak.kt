package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mosscoat Goriak
 * {2}{G}
 * Creature — Beast
 * 2/4
 * Vigilance
 */
val MosscoatGoriak = card("Mosscoat Goriak") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 4
    oracleText = "Vigilance"

    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "167"
        artist = "Dan Murayama Scott"
        flavorText = "\"Goriaks are as stubborn and hardy as I'd expect of a large herbivore in a wetland habitat. But I never expected them to have such beautiful voices!\"\n—Vivien Reid"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c23139d4-0db5-4683-8d49-f4600fbe29e2.jpg"
    }
}
