package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Avatar: The Last Airbender Basic Lands
 *
 * One art variant of each basic land type, collector numbers 282-286.
 */

val TlaPlains282 = basicLand("Plains") {
    collectorNumber = "282"
    artist = "Slawek Fedorczuk"
    imageUri = "https://cards.scryfall.io/normal/front/4/0/4069fb4a-8ee1-41ef-ab93-39a8cc58e0e5.jpg?1764122051"
}

val TlaIsland283 = basicLand("Island") {
    collectorNumber = "283"
    artist = "Maojin Lee"
    imageUri = "https://cards.scryfall.io/normal/front/a/2/a2e22347-f0cb-4cfd-88a3-4f46a16e4946.jpg?1764122056"
}

val TlaSwamp284 = basicLand("Swamp") {
    collectorNumber = "284"
    artist = "Matteo Bassini"
    imageUri = "https://cards.scryfall.io/normal/front/f/0/f0b234d8-d6bb-48ec-8a4d-d8a570a69c62.jpg?1764122062"
}

val TlaMountain285 = basicLand("Mountain") {
    collectorNumber = "285"
    artist = "Salvatorre Zee Yazzie"
    imageUri = "https://cards.scryfall.io/normal/front/c/4/c44f81ca-f72f-445c-8901-3a894a2a47f9.jpg?1764122068"
}

val TlaForest286 = basicLand("Forest") {
    collectorNumber = "286"
    artist = "Maojin Lee"
    imageUri = "https://cards.scryfall.io/normal/front/a/3/a305e44f-4253-4754-b83f-1e34103d77b0.jpg?1767738379"
}

/**
 * All Avatar: The Last Airbender basic land variants.
 */
val AvatarTheLastAirbenderBasicLands = listOf(
    TlaPlains282,
    TlaIsland283,
    TlaSwamp284,
    TlaMountain285,
    TlaForest286,
)
