package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Kaladesh Basic Lands
 *
 * Kaladesh contains 3 art variants of each basic land type.
 * Cards 250-264 (Plains 250-252, Island 253-255, Swamp 256-258, Mountain 259-261, Forest 262-264)
 */

// =============================================================================
// Plains (Cards 250-252)
// =============================================================================

val Plains250 = basicLand("Plains") {
    collectorNumber = "250"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/3/2/32912b82-bbe5-4d70-817d-cd18bfdecacb.jpg?1783937142"
}

val Plains251 = basicLand("Plains") {
    collectorNumber = "251"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/2/1/21e41416-bc0f-4e49-9137-df8572e91ae5.jpg?1783937141"
}

val Plains252 = basicLand("Plains") {
    collectorNumber = "252"
    artist = "Clint Cearley"
    imageUri = "https://cards.scryfall.io/normal/front/8/7/879162e8-3a10-40da-8d86-9a7dff67c961.jpg?1783937141"
}

// =============================================================================
// Island (Cards 253-255)
// =============================================================================

val Island253 = basicLand("Island") {
    collectorNumber = "253"
    artist = "Yeong-Hao Han"
    imageUri = "https://cards.scryfall.io/normal/front/8/c/8ca139d8-08a1-45d4-be9d-2ee5c9b3de43.jpg?1783937140"
}

val Island254 = basicLand("Island") {
    collectorNumber = "254"
    artist = "Yeong-Hao Han"
    imageUri = "https://cards.scryfall.io/normal/front/0/8/086d6aed-1f7e-4a80-8d66-01d995f344ed.jpg?1783937141"
}

val Island255 = basicLand("Island") {
    collectorNumber = "255"
    artist = "Johannes Voss"
    imageUri = "https://cards.scryfall.io/normal/front/1/9/197080a9-ebb5-4a8b-81f8-0368c5bba35a.jpg?1783937139"
}

// =============================================================================
// Swamp (Cards 256-258)
// =============================================================================

val Swamp256 = basicLand("Swamp") {
    collectorNumber = "256"
    artist = "James Paick"
    imageUri = "https://cards.scryfall.io/normal/front/4/7/4760cdcc-e973-439c-a74a-5cb73b4fa22f.jpg?1783937139"
}

val Swamp257 = basicLand("Swamp") {
    collectorNumber = "257"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/f/5/f5a9f8ee-1416-4be8-9da1-f4546164c522.jpg?1783937140"
}

val Swamp258 = basicLand("Swamp") {
    collectorNumber = "258"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/3/6/364ee0ec-4970-4a30-bf5b-b045a6122860.jpg?1783937139"
}

// =============================================================================
// Mountain (Cards 259-261)
// =============================================================================

val Mountain259 = basicLand("Mountain") {
    collectorNumber = "259"
    artist = "Richard Wright"
    imageUri = "https://cards.scryfall.io/normal/front/2/9/29b4a0e0-3d27-414b-80cb-352f16389a83.jpg?1783937138"
}

val Mountain260 = basicLand("Mountain") {
    collectorNumber = "260"
    artist = "Eytan Zana"
    imageUri = "https://cards.scryfall.io/normal/front/d/7/d72d883e-d194-4154-a96c-35e601c5f797.jpg?1783937138"
}

val Mountain261 = basicLand("Mountain") {
    collectorNumber = "261"
    artist = "Eytan Zana"
    imageUri = "https://cards.scryfall.io/normal/front/5/6/56b65f77-85c1-49cd-bb2e-70e5225ace9a.jpg?1783937138"
}

// =============================================================================
// Forest (Cards 262-264)
// =============================================================================

val Forest262 = basicLand("Forest") {
    collectorNumber = "262"
    artist = "Christine Choi"
    imageUri = "https://cards.scryfall.io/normal/front/9/2/92e67efe-cc8a-4132-9019-26ddfc72a735.jpg?1783937137"
}

val Forest263 = basicLand("Forest") {
    collectorNumber = "263"
    artist = "Christine Choi"
    imageUri = "https://cards.scryfall.io/normal/front/f/2/f2864a75-2945-4467-be00-3b4b71831f4f.jpg?1783937137"
}

val Forest264 = basicLand("Forest") {
    collectorNumber = "264"
    artist = "Chase Stone"
    imageUri = "https://cards.scryfall.io/normal/front/b/f/bf8bd63e-303e-402c-9ad2-4f6e55ed0e14.jpg?1783937137"
}

/**
 * All Kaladesh basic land variants.
 */
val KaladeshBasicLands = listOf(
    Plains250, Plains251, Plains252,
    Island253, Island254, Island255,
    Swamp256, Swamp257, Swamp258,
    Mountain259, Mountain260, Mountain261,
    Forest262, Forest263, Forest264,
)
