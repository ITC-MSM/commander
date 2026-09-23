package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Reality Fracture Island arts: two regular-frame booster arts in the main set numbering, then
 * three non-booster treatments above it. Scryfall reports `booster: false` for every FRA basic
 * (pre-release data), so the regular arts are marked in-booster by hand — they are what
 * limited deck building hands out (`BoosterGenerator.getBasicLands`).
 */
val RealityFractureIsland283 = basicLand("Island") {
    collectorNumber = "283"
    artist = "Chris Ostrowski"
    imageUri = "https://cards.scryfall.io/normal/front/d/8/d8184e92-54e6-4ddc-86e8-67f5c5eb079d.jpg?1789599732"
}

val RealityFractureIsland284 = basicLand("Island") {
    collectorNumber = "284"
    artist = "Zack Stella"
    imageUri = "https://cards.scryfall.io/normal/front/1/c/1c1b233f-ade1-40c8-a91f-e9d8cc63179b.jpg?1789599692"
}

val RealityFractureIsland385 = basicLand("Island") {
    collectorNumber = "385"
    artist = "Calder Moore"
    imageUri = "https://cards.scryfall.io/normal/front/b/c/bc4cd881-9c34-4cf9-8afe-050e803f34a6.jpg?1788878353"
    inBooster = false
}

val RealityFractureIsland386 = basicLand("Island") {
    collectorNumber = "386"
    artist = "Calder Moore"
    imageUri = "https://cards.scryfall.io/normal/front/6/f/6ff3f00e-4ec2-41ef-bd19-fa830480245d.jpg?1788878355"
    inBooster = false
}

val RealityFractureIsland387 = basicLand("Island") {
    collectorNumber = "387"
    artist = "Calder Moore"
    imageUri = "https://cards.scryfall.io/normal/front/b/2/b256a981-ba4b-43b9-83a3-f31a105a2c26.jpg?1788878361"
    inBooster = false
}
