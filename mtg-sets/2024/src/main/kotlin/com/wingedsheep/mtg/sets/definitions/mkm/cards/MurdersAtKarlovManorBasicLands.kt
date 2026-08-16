package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Murders at Karlov Manor Basic Lands
 *
 * MKM contains 3 art variants of each basic land type, all booster-eligible.
 * Cards 272-276 (Mia Boas full-art set) and 277-286 (two additional artists per type).
 */

// =============================================================================
// Plains (Cards 272, 277, 278)
// =============================================================================

val MkmPlains272 = basicLand("Plains") {
    collectorNumber = "272"
    artist = "Mia Boas"
    imageUri = "https://cards.scryfall.io/normal/front/5/9/598f857d-ee17-4478-bb39-cc3ab77ab8d8.jpg?1783912822"
}

val MkmPlains277 = basicLand("Plains") {
    collectorNumber = "277"
    artist = "Muhammad Firdaus"
    imageUri = "https://cards.scryfall.io/normal/front/8/1/81a1a8a6-916b-4eef-8079-6775afcb63cf.jpg?1783912819"
}

val MkmPlains278 = basicLand("Plains") {
    collectorNumber = "278"
    artist = "Carlos Palma Cruchaga"
    imageUri = "https://cards.scryfall.io/normal/front/e/c/ec05cb6c-6e7f-4d40-ba38-a9fc06158094.jpg?1783912818"
}

// =============================================================================
// Island (Cards 273, 279, 280)
// =============================================================================

val MkmIsland273 = basicLand("Island") {
    collectorNumber = "273"
    artist = "Mia Boas"
    imageUri = "https://cards.scryfall.io/normal/front/f/e/fe5f01e9-f85f-41e8-9527-88f76e7bfc02.jpg?1783912818"
}

val MkmIsland279 = basicLand("Island") {
    collectorNumber = "279"
    artist = "Jorge Jacinto"
    imageUri = "https://cards.scryfall.io/normal/front/6/9/6908b583-4a52-4819-82a3-9db9c860aeb6.jpg?1783912819"
}

val MkmIsland280 = basicLand("Island") {
    collectorNumber = "280"
    artist = "Titus Lunter"
    imageUri = "https://cards.scryfall.io/normal/front/d/f/df18762d-9980-4344-9d4f-e9d2cd8e0456.jpg?1783912819"
}

// =============================================================================
// Swamp (Cards 274, 281, 282)
// =============================================================================

val MkmSwamp274 = basicLand("Swamp") {
    collectorNumber = "274"
    artist = "Mia Boas"
    imageUri = "https://cards.scryfall.io/normal/front/5/3/53d0a415-26b8-4ba2-9503-b4ee2b93617c.jpg?1783912818"
}

val MkmSwamp281 = basicLand("Swamp") {
    collectorNumber = "281"
    artist = "Carlos Palma Cruchaga"
    imageUri = "https://cards.scryfall.io/normal/front/7/0/70d0ef58-c0b3-4bcd-b470-0cf41219a4e6.jpg?1783912818"
}

val MkmSwamp282 = basicLand("Swamp") {
    collectorNumber = "282"
    artist = "Carlos Palma Cruchaga"
    imageUri = "https://cards.scryfall.io/normal/front/b/1/b1cbccfc-3585-4895-94d1-c3a67205e5fe.jpg?1783912817"
}

// =============================================================================
// Mountain (Cards 275, 283, 284)
// =============================================================================

val MkmMountain275 = basicLand("Mountain") {
    collectorNumber = "275"
    artist = "Mia Boas"
    imageUri = "https://cards.scryfall.io/normal/front/5/3/5383b66e-e559-47d2-9967-9c2d5f898653.jpg?1783912817"
}

val MkmMountain283 = basicLand("Mountain") {
    collectorNumber = "283"
    artist = "Jorge Jacinto"
    imageUri = "https://cards.scryfall.io/normal/front/d/e/de754d20-5371-456b-9064-8f0687d2eab7.jpg?1783912816"
}

val MkmMountain284 = basicLand("Mountain") {
    collectorNumber = "284"
    artist = "Svetlin Velinov"
    imageUri = "https://cards.scryfall.io/normal/front/c/d/cd8a0cd6-8d3b-44bd-b609-c12fb851d17b.jpg?1783912818"
}

// =============================================================================
// Forest (Cards 276, 285, 286)
// =============================================================================

val MkmForest276 = basicLand("Forest") {
    collectorNumber = "276"
    artist = "Mia Boas"
    imageUri = "https://cards.scryfall.io/normal/front/8/1/8126b842-ea58-4988-8b3f-0394cf766b91.jpg?1783912818"
}

val MkmForest285 = basicLand("Forest") {
    collectorNumber = "285"
    artist = "Jorge Jacinto"
    imageUri = "https://cards.scryfall.io/normal/front/c/c/cc485069-e081-4e83-bbad-d5faf7a5bd03.jpg?1783912816"
}

val MkmForest286 = basicLand("Forest") {
    collectorNumber = "286"
    artist = "Carlos Palma Cruchaga"
    imageUri = "https://cards.scryfall.io/normal/front/6/c/6cb8f264-cc26-476e-a3b7-53638dce7395.jpg?1783912817"
}

/**
 * All Murders at Karlov Manor basic land variants.
 */
val MurdersAtKarlovManorBasicLands = listOf(
    MkmPlains272, MkmPlains277, MkmPlains278,
    MkmIsland273, MkmIsland279, MkmIsland280,
    MkmSwamp274, MkmSwamp281, MkmSwamp282,
    MkmMountain275, MkmMountain283, MkmMountain284,
    MkmForest276, MkmForest285, MkmForest286,
)
