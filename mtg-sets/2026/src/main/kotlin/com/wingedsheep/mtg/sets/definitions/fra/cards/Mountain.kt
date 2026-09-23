package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Reality Fracture Mountain arts: two regular-frame booster arts in the main set numbering, then
 * three non-booster treatments above it. Scryfall reports `booster: false` for every FRA basic
 * (pre-release data), so the regular arts are marked in-booster by hand — they are what
 * limited deck building hands out (`BoosterGenerator.getBasicLands`).
 */
val RealityFractureMountain287 = basicLand("Mountain") {
    collectorNumber = "287"
    artist = "Jorge Jacinto"
    imageUri = "https://cards.scryfall.io/normal/front/1/6/16671d98-6f00-477b-a010-d2905c94eb65.jpg?1789599693"
}

val RealityFractureMountain288 = basicLand("Mountain") {
    collectorNumber = "288"
    artist = "Chris Ostrowski"
    imageUri = "https://cards.scryfall.io/normal/front/e/d/ed8ebf4a-2879-4da2-93d8-b63d782a7786.jpg?1789599698"
}

val RealityFractureMountain391 = basicLand("Mountain") {
    collectorNumber = "391"
    artist = "Leon Tukker"
    imageUri = "https://cards.scryfall.io/normal/front/e/c/ec2ceca8-2d90-4d8f-ac47-cd8134e8d859.jpg?1788878368"
    inBooster = false
}

val RealityFractureMountain392 = basicLand("Mountain") {
    collectorNumber = "392"
    artist = "Leon Tukker"
    imageUri = "https://cards.scryfall.io/normal/front/d/d/ddc91faf-dd36-4c26-b3b4-0a6a624b5663.jpg?1788878370"
    inBooster = false
}

val RealityFractureMountain393 = basicLand("Mountain") {
    collectorNumber = "393"
    artist = "Leon Tukker"
    imageUri = "https://cards.scryfall.io/normal/front/a/4/a49ae3b7-4b1f-41b6-8c05-cbdc44f070cb.jpg?1788878371"
    inBooster = false
}
