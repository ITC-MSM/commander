package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Reality Fracture Swamp arts: two regular-frame booster arts in the main set numbering, then
 * three non-booster treatments above it. Scryfall reports `booster: false` for every FRA basic
 * (pre-release data), so the regular arts are marked in-booster by hand — they are what
 * limited deck building hands out (`BoosterGenerator.getBasicLands`).
 */
val RealityFractureSwamp285 = basicLand("Swamp") {
    collectorNumber = "285"
    artist = "Josu Solano"
    imageUri = "https://cards.scryfall.io/normal/front/3/0/30693b85-550d-4c98-8c5b-4fd4e91c9f28.jpg?1789599699"
}

val RealityFractureSwamp286 = basicLand("Swamp") {
    collectorNumber = "286"
    artist = "Leon Tukker"
    imageUri = "https://cards.scryfall.io/normal/front/0/5/05465ed0-f511-4e6f-9561-49bf6608fb80.jpg?1789599687"
}

val RealityFractureSwamp388 = basicLand("Swamp") {
    collectorNumber = "388"
    artist = "Daniel Romanovsky"
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e8418418-10bb-48e1-83ed-0081390384b4.jpg?1788878357"
    inBooster = false
}

val RealityFractureSwamp389 = basicLand("Swamp") {
    collectorNumber = "389"
    artist = "Daniel Romanovsky"
    imageUri = "https://cards.scryfall.io/normal/front/1/4/140ba675-3922-48cf-b6ec-8639267dcab8.jpg?1788878359"
    inBooster = false
}

val RealityFractureSwamp390 = basicLand("Swamp") {
    collectorNumber = "390"
    artist = "Daniel Romanovsky"
    imageUri = "https://cards.scryfall.io/normal/front/f/0/f080e0af-cd86-488b-968e-e69b0fa0fe93.jpg?1788878362"
    inBooster = false
}
