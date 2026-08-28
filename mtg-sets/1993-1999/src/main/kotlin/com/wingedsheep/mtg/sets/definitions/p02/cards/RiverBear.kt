package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * River Bear
 * {3}{G}
 * Creature — Bear
 * 3/3
 * Islandwalk (This creature can't be blocked as long as defending player controls an Island.)
 */
val RiverBear = card("River Bear") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bear"
    oracleText = "Islandwalk (This creature can't be blocked as long as defending player controls an Island.)"
    power = 3
    toughness = 3
    keywords(Keyword.ISLANDWALK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "144"
        artist = "Una Fricker"
        flavorText = "The bears had been isolated on an island for hundreds of years, until the island sank and the bears didn't."
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8508e3b-a934-4795-a2df-07e792f2685f.jpg"
    }
}
