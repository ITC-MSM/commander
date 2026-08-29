package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Theros Beyond Death Basic Lands
 *
 * Theros Beyond Death prints three art variants of each basic land type:
 * the booster basics (250-254) and two further variants each (278-287).
 */

// =============================================================================
// Plains (Cards 250, 278, 279)
// =============================================================================

val TherosBeyondDeathPlains250 = basicLand("Plains") {
    collectorNumber = "250"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/a/9/a9891b7b-fc52-470c-9f74-292ae665f378.jpg"
}

val TherosBeyondDeathPlains278 = basicLand("Plains") {
    collectorNumber = "278"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/4/0/40aca5ca-a37b-4919-aef6-2510b4779161.jpg"
}

val TherosBeyondDeathPlains279 = basicLand("Plains") {
    collectorNumber = "279"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/d/b/db68b6a3-10e5-42d1-9325-94a4a821782a.jpg"
}

// =============================================================================
// Island (Cards 251, 280, 281)
// =============================================================================

val TherosBeyondDeathIsland251 = basicLand("Island") {
    collectorNumber = "251"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/a/c/acf7b664-3e75-4018-81f6-2a14ab59f258.jpg"
}

val TherosBeyondDeathIsland280 = basicLand("Island") {
    collectorNumber = "280"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/9/2/92daaa39-cd2f-4c03-8f41-92d99d0a3366.jpg"
}

val TherosBeyondDeathIsland281 = basicLand("Island") {
    collectorNumber = "281"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/b/8/b82c12c2-2ebf-470b-b0d2-92ccc5faa056.jpg"
}

// =============================================================================
// Swamp (Cards 252, 282, 283)
// =============================================================================

val TherosBeyondDeathSwamp252 = basicLand("Swamp") {
    collectorNumber = "252"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/0/2/02cb5cfd-018e-4c5e-bef1-166262aa5f1d.jpg"
}

val TherosBeyondDeathSwamp282 = basicLand("Swamp") {
    collectorNumber = "282"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/6/6/66bb5192-58bc-4efe-a145-2e804fd3483d.jpg"
}

val TherosBeyondDeathSwamp283 = basicLand("Swamp") {
    collectorNumber = "283"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/e/5/e54a44f2-70bf-4782-bd13-9d03e109d60d.jpg"
}

// =============================================================================
// Mountain (Cards 253, 284, 285)
// =============================================================================

val TherosBeyondDeathMountain253 = basicLand("Mountain") {
    collectorNumber = "253"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/5/3/53fb7b99-9e47-46a6-9c8a-88e28b5197f1.jpg"
}

val TherosBeyondDeathMountain284 = basicLand("Mountain") {
    collectorNumber = "284"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/d/c/dc3f4154-9347-4ceb-8744-9f1ace90d33f.jpg"
}

val TherosBeyondDeathMountain285 = basicLand("Mountain") {
    collectorNumber = "285"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/d/1/d10d759b-db5b-4a59-840c-05bcbf2381f3.jpg"
}

// =============================================================================
// Forest (Cards 254, 286, 287)
// =============================================================================

val TherosBeyondDeathForest254 = basicLand("Forest") {
    collectorNumber = "254"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/3/2/32af9f41-89e2-4e7a-9fec-fffe79cae077.jpg"
}

val TherosBeyondDeathForest286 = basicLand("Forest") {
    collectorNumber = "286"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/c/4/c4be31c4-9cb3-4a07-865b-5621127df660.jpg"
}

val TherosBeyondDeathForest287 = basicLand("Forest") {
    collectorNumber = "287"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/6/a/6af0c659-f182-4ad4-bca7-e6c3377f808d.jpg"
}

/**
 * All Theros Beyond Death basic land variants.
 */
val TherosBeyondDeathBasicLands = listOf(
    TherosBeyondDeathPlains250, TherosBeyondDeathPlains278, TherosBeyondDeathPlains279,
    TherosBeyondDeathIsland251, TherosBeyondDeathIsland280, TherosBeyondDeathIsland281,
    TherosBeyondDeathSwamp252, TherosBeyondDeathSwamp282, TherosBeyondDeathSwamp283,
    TherosBeyondDeathMountain253, TherosBeyondDeathMountain284, TherosBeyondDeathMountain285,
    TherosBeyondDeathForest254, TherosBeyondDeathForest286, TherosBeyondDeathForest287
)
