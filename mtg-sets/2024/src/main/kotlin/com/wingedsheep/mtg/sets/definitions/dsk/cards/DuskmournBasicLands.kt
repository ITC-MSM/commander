package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Duskmourn: House of Horror Basic Lands
 *
 * Duskmourn contains 3 art variants of each basic land type.
 * Cards 272-276 (Dan Mumford full set) and 277-286 (two additional artists per type).
 */

// =============================================================================
// Plains (Cards 272, 277, 278)
// =============================================================================

val DuskmournPlains272 = basicLand("Plains") {
    collectorNumber = "272"
    artist = "Dan Mumford"
    imageUri = "https://cards.scryfall.io/normal/front/e/6/e67ce864-bf29-42f1-81ca-a98022892eec.jpg?1726286890"
}

val DuskmournPlains277 = basicLand("Plains") {
    collectorNumber = "277"
    artist = "Marco Gorlei"
    imageUri = "https://cards.scryfall.io/normal/front/1/b/1b499b37-efaf-4484-95e8-a70a9778c804.jpg?1726286908"
}

val DuskmournPlains278 = basicLand("Plains") {
    collectorNumber = "278"
    artist = "Josu Hernaiz"
    imageUri = "https://cards.scryfall.io/normal/front/7/5/756abff9-9810-4e2d-b1d7-ec4e2d6d5187.jpg?1726286912"
}

// =============================================================================
// Island (Cards 273, 279, 280)
// =============================================================================

val DuskmournIsland273 = basicLand("Island") {
    collectorNumber = "273"
    artist = "Dan Mumford"
    imageUri = "https://cards.scryfall.io/normal/front/4/0/40e3bf00-84cc-498c-b214-1052b4904d92.jpg?1726286894"
}

val DuskmournIsland279 = basicLand("Island") {
    collectorNumber = "279"
    artist = "Raymond Bonilla"
    imageUri = "https://cards.scryfall.io/normal/front/9/4/947702ca-d065-4368-9f26-f859d4642cb6.jpg?1726286915"
}

val DuskmournIsland280 = basicLand("Island") {
    collectorNumber = "280"
    artist = "Leonardo Borazio"
    imageUri = "https://cards.scryfall.io/normal/front/e/1/e1d10d9c-8771-4870-aebf-e767d0fada32.jpg?1726286920"
}

// =============================================================================
// Swamp (Cards 274, 281, 282)
// =============================================================================

val DuskmournSwamp274 = basicLand("Swamp") {
    collectorNumber = "274"
    artist = "Dan Mumford"
    imageUri = "https://cards.scryfall.io/normal/front/7/4/7442c1c7-1c10-4387-92e6-4bdea263064f.jpg?1726286897"
}

val DuskmournSwamp281 = basicLand("Swamp") {
    collectorNumber = "281"
    artist = "Martin de Diego Sádaba"
    imageUri = "https://cards.scryfall.io/normal/front/3/c/3c51de66-a3ed-4ca9-befb-9813e96c4ade.jpg?1726286923"
}

val DuskmournSwamp282 = basicLand("Swamp") {
    collectorNumber = "282"
    artist = "Néstor Ossandón Leal"
    imageUri = "https://cards.scryfall.io/normal/front/c/b/cbd95de0-702a-4b88-a1cc-981cf1d9673e.jpg?1726286927"
}

// =============================================================================
// Mountain (Cards 275, 283, 284)
// =============================================================================

val DuskmournMountain275 = basicLand("Mountain") {
    collectorNumber = "275"
    artist = "Dan Mumford"
    imageUri = "https://cards.scryfall.io/normal/front/8/c/8c8841b2-9b4e-4f65-8e30-1e9423cb8fbc.jpg?1726286901"
}

val DuskmournMountain283 = basicLand("Mountain") {
    collectorNumber = "283"
    artist = "Ralph Horsley"
    imageUri = "https://cards.scryfall.io/normal/front/a/c/accb56f0-3903-4894-86f0-3965162064d4.jpg?1726286930"
}

val DuskmournMountain284 = basicLand("Mountain") {
    collectorNumber = "284"
    artist = "Néstor Ossandón Leal"
    imageUri = "https://cards.scryfall.io/normal/front/1/a/1a44d8a4-21de-497d-9eed-702d7b592728.jpg?1726286935"
}

// =============================================================================
// Forest (Cards 276, 285, 286)
// =============================================================================

val DuskmournForest276 = basicLand("Forest") {
    collectorNumber = "276"
    artist = "Dan Mumford"
    imageUri = "https://cards.scryfall.io/normal/front/b/a/ba2a4cbb-f325-4178-80db-7090f5672414.jpg?1726286905"
}

val DuskmournForest285 = basicLand("Forest") {
    collectorNumber = "285"
    artist = "Martin de Diego Sádaba"
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e84c0880-728d-4ac2-b685-4f18f66c24db.jpg?1726286938"
}

val DuskmournForest286 = basicLand("Forest") {
    collectorNumber = "286"
    artist = "Josu Hernaiz"
    imageUri = "https://cards.scryfall.io/normal/front/0/d/0da5fbc2-24ad-4520-a60a-436d3a485fec.jpg?1726286943"
}

/**
 * All Duskmourn: House of Horror basic land variants.
 */
val DuskmournBasicLands = listOf(
    DuskmournPlains272, DuskmournPlains277, DuskmournPlains278,
    DuskmournIsland273, DuskmournIsland279, DuskmournIsland280,
    DuskmournSwamp274, DuskmournSwamp281, DuskmournSwamp282,
    DuskmournMountain275, DuskmournMountain283, DuskmournMountain284,
    DuskmournForest276, DuskmournForest285, DuskmournForest286,
)
