package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Reality Fracture Plains arts: two regular-frame booster arts in the main set numbering, then
 * three non-booster treatments above it. Scryfall reports `booster: false` for every FRA basic
 * (pre-release data), so the regular arts are marked in-booster by hand — they are what
 * limited deck building hands out (`BoosterGenerator.getBasicLands`).
 */
val RealityFracturePlains281 = basicLand("Plains") {
    collectorNumber = "281"
    artist = "Luc Courtois"
    imageUri = "https://cards.scryfall.io/normal/front/a/4/a4eaecb8-066a-4c72-aa5d-5ed0614ba537.jpg?1789599702"
}

val RealityFracturePlains282 = basicLand("Plains") {
    collectorNumber = "282"
    artist = "Florian Herold"
    imageUri = "https://cards.scryfall.io/normal/front/d/5/d5955c78-83ec-43d1-b7d6-214ab5d32ff2.jpg?1789599732"
}

val RealityFracturePlains382 = basicLand("Plains") {
    collectorNumber = "382"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/7/9/796b72af-5078-427d-b566-9bccad17090f.jpg?1788878349"
    inBooster = false
}

val RealityFracturePlains383 = basicLand("Plains") {
    collectorNumber = "383"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/5/7/57999b70-74c6-415a-aef8-3afc546e7557.jpg?1788878349"
    inBooster = false
}

val RealityFracturePlains384 = basicLand("Plains") {
    collectorNumber = "384"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/4/8/48964496-cbbb-4646-a257-153497bbc83d.jpg?1788878351"
    inBooster = false
}
