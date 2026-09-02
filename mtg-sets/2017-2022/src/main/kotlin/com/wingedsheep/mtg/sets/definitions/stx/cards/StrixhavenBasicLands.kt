package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Strixhaven: School of Mages Basic Lands
 *
 * Strixhaven's ten basics, cards 366-375: two illustrations of each type, one per pair of the
 * five colleges' grounds. All are the plain, non-snow basics, so each is a [basicLand] val that
 * [StrixhavenSchoolOfMagesSet.basicLands] discovers by package — never a `Printing` row.
 */

val StrixhavenPlains366 = basicLand("Plains") {
    collectorNumber = "366"
    artist = "Jonas De Ro"
    imageUri = "https://cards.scryfall.io/normal/front/a/b/abadcf9e-46ed-4a8b-888c-0cd3756bc8ab.jpg"
}

val StrixhavenPlains367 = basicLand("Plains") {
    collectorNumber = "367"
    artist = "Titus Lunter"
    imageUri = "https://cards.scryfall.io/normal/front/4/9/49f7e5de-1fa8-406e-a411-fc8a11937000.jpg"
}

val StrixhavenIsland368 = basicLand("Island") {
    collectorNumber = "368"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/c/9/c96f9fb6-ef52-43f9-a458-8602a7c83333.jpg"
}

val StrixhavenIsland369 = basicLand("Island") {
    collectorNumber = "369"
    artist = "Lucas Staniec"
    imageUri = "https://cards.scryfall.io/normal/front/8/4/849a217f-d532-4a7c-bcbf-d127641f6edf.jpg"
}

val StrixhavenSwamp370 = basicLand("Swamp") {
    collectorNumber = "370"
    artist = "Titus Lunter"
    imageUri = "https://cards.scryfall.io/normal/front/1/3/13bd1c69-2561-4ff1-af00-bb519b3897c2.jpg"
}

val StrixhavenSwamp371 = basicLand("Swamp") {
    collectorNumber = "371"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/1/1/11093e3f-092e-49d9-aef9-b9855b040bf2.jpg"
}

val StrixhavenMountain372 = basicLand("Mountain") {
    collectorNumber = "372"
    artist = "Jonas De Ro"
    imageUri = "https://cards.scryfall.io/normal/front/9/6/9660fb20-f499-4f7a-9f25-e463f095ab90.jpg"
}

val StrixhavenMountain373 = basicLand("Mountain") {
    collectorNumber = "373"
    artist = "Grady Frederick"
    imageUri = "https://cards.scryfall.io/normal/front/f/b/fb8fffd1-aff1-4aad-bfa0-9907adb5ce25.jpg"
}

val StrixhavenForest374 = basicLand("Forest") {
    collectorNumber = "374"
    artist = "Grady Frederick"
    imageUri = "https://cards.scryfall.io/normal/front/d/f/dfaf517f-86a1-45eb-bd71-e0bffb610396.jpg"
}

val StrixhavenForest375 = basicLand("Forest") {
    collectorNumber = "375"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/6/5/659a7d45-af0d-4a4a-a878-e8e40b732bfa.jpg"
}

/**
 * All Strixhaven basic land variants.
 */
val StrixhavenBasicLands = listOf(
    StrixhavenPlains366,
    StrixhavenPlains367,
    StrixhavenIsland368,
    StrixhavenIsland369,
    StrixhavenSwamp370,
    StrixhavenSwamp371,
    StrixhavenMountain372,
    StrixhavenMountain373,
    StrixhavenForest374,
    StrixhavenForest375
)
