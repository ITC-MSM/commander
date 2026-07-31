package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Aetherdrift Basic Lands
 *
 * Aetherdrift contains one basic land of each type (cards 277, 280, 283, 286, and 289).
 */

val AetherdriftPlains277 = basicLand("Plains") {
    collectorNumber = "277"
    artist = "Samuele Bandini"
    imageUri = "https://cards.scryfall.io/normal/front/4/6/464723b3-0723-45f2-a258-32d098c39039.jpg?1783907835"
}

val AetherdriftIsland280 = basicLand("Island") {
    collectorNumber = "280"
    artist = "Samuele Bandini"
    imageUri = "https://cards.scryfall.io/normal/front/5/a/5a8e9b9e-4947-4b6a-b21b-6fe009760fc5.jpg?1783907834"
}

val AetherdriftSwamp283 = basicLand("Swamp") {
    collectorNumber = "283"
    artist = "Samuele Bandini"
    imageUri = "https://cards.scryfall.io/normal/front/0/4/040d064e-b023-4750-afeb-1f58e36bc4ab.jpg?1783907831"
}

val AetherdriftMountain286 = basicLand("Mountain") {
    collectorNumber = "286"
    artist = "Samuele Bandini"
    imageUri = "https://cards.scryfall.io/normal/front/f/b/fb76cb6c-2f4d-4ca2-876f-d49ea529e59b.jpg?1783907832"
}

val AetherdriftForest289 = basicLand("Forest") {
    collectorNumber = "289"
    artist = "Samuele Bandini"
    imageUri = "https://cards.scryfall.io/normal/front/b/6/b69adddd-3626-45d6-8100-26cf1f314d00.jpg?1783907831"
}

val AetherdriftBasicLands = listOf(
    AetherdriftPlains277,
    AetherdriftIsland280,
    AetherdriftSwamp283,
    AetherdriftMountain286,
    AetherdriftForest289,
)
