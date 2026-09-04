package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Rivals of Ixalan Basic Lands
 *
 * Rivals of Ixalan contains a single art variant of each basic land type, all by Dimitar Marinski.
 * Cards 192-196 (Plains 192, Island 193, Swamp 194, Mountain 195, Forest 196)
 */

val RivalsOfIxalanPlains192 = basicLand("Plains") {
    collectorNumber = "192"
    artist = "Dimitar Marinski"
    imageUri = "https://cards.scryfall.io/normal/front/9/2/92342f45-ccfe-4d94-82ba-2fd55128326f.jpg?1783935262"
}

val RivalsOfIxalanIsland193 = basicLand("Island") {
    collectorNumber = "193"
    artist = "Dimitar Marinski"
    imageUri = "https://cards.scryfall.io/normal/front/b/2/b22fb908-e616-48ce-a388-46eabe26221a.jpg?1783935260"
}

val RivalsOfIxalanSwamp194 = basicLand("Swamp") {
    collectorNumber = "194"
    artist = "Dimitar Marinski"
    imageUri = "https://cards.scryfall.io/normal/front/6/7/674de0e1-be2d-4fda-8519-8775802a7b36.jpg?1783935259"
}

val RivalsOfIxalanMountain195 = basicLand("Mountain") {
    collectorNumber = "195"
    artist = "Dimitar Marinski"
    imageUri = "https://cards.scryfall.io/normal/front/5/f/5fd9acc5-0088-4621-8f48-e997da5f27c5.jpg?1783935260"
}

val RivalsOfIxalanForest196 = basicLand("Forest") {
    collectorNumber = "196"
    artist = "Dimitar Marinski"
    imageUri = "https://cards.scryfall.io/normal/front/9/2/92c9e16d-525b-4ba9-890d-fa2719206ba0.jpg?1783935259"
}

/**
 * All Rivals of Ixalan basic land variants.
 */
val RivalsOfIxalanBasicLands = listOf(
    RivalsOfIxalanPlains192,
    RivalsOfIxalanIsland193,
    RivalsOfIxalanSwamp194,
    RivalsOfIxalanMountain195,
    RivalsOfIxalanForest196,
)
