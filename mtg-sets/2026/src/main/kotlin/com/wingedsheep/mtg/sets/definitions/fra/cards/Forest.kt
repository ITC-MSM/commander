package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Reality Fracture Forest arts: two regular-frame booster arts in the main set numbering, then
 * three non-booster treatments above it. Scryfall reports `booster: false` for every FRA basic
 * (pre-release data), so the regular arts are marked in-booster by hand — they are what
 * limited deck building hands out (`BoosterGenerator.getBasicLands`).
 */
val RealityFractureForest289 = basicLand("Forest") {
    collectorNumber = "289"
    artist = "Randy Gallegos"
    imageUri = "https://cards.scryfall.io/normal/front/6/1/613bc075-2fe4-421f-bfec-3786a5e37797.jpg?1789729776"
}

val RealityFractureForest290 = basicLand("Forest") {
    collectorNumber = "290"
    artist = "Chris Ostrowski"
    imageUri = "https://cards.scryfall.io/normal/front/2/5/25e36841-3242-436b-8e82-9099063fa461.jpg?1789729779"
}

val RealityFractureForest394 = basicLand("Forest") {
    collectorNumber = "394"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/7/9/790dcf48-ab51-4b10-9933-6b7e3baf0f52.jpg?1788878375"
    inBooster = false
}

val RealityFractureForest395 = basicLand("Forest") {
    collectorNumber = "395"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/1/c/1c2ac08e-3810-46f4-94ab-8b32986f9185.jpg?1788878378"
    inBooster = false
}

val RealityFractureForest396 = basicLand("Forest") {
    collectorNumber = "396"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/a/0/a0d12fc5-c7bd-4734-9259-98fe65241f60.jpg?1788878373"
    inBooster = false
}
