package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Ice Age Basic Lands
 *
 * Ice Age prints three art variants of each of the five basic land types (cards 364-383,
 * interleaved with the set's four snow-covered basics). The snow-covered lands are *not* here:
 * `basicLand(...)` hardcodes the "Basic Land — <type>" type line and cannot carry the Snow
 * supertype, so each of those is a hand-written `card(...)` of its own.
 */

// =============================================================================
// Plains (Cards 364-366)
// =============================================================================

val IceAgePlains364 = basicLand("Plains") {
    collectorNumber = "364"
    artist = "Christopher Rush"
    imageUri = "https://cards.scryfall.io/normal/front/7/b/7b68bdb0-41cc-48f6-905e-7da1ff4ba5e0.jpg?1783947450"
}

val IceAgePlains365 = basicLand("Plains") {
    collectorNumber = "365"
    artist = "Christopher Rush"
    imageUri = "https://cards.scryfall.io/normal/front/d/f/df3e94f7-9f97-4652-a1f1-381feb15f688.jpg?1783947450"
}

val IceAgePlains366 = basicLand("Plains") {
    collectorNumber = "366"
    artist = "Christopher Rush"
    imageUri = "https://cards.scryfall.io/normal/front/2/7/27ac1fc7-0698-4a94-8353-cc4c13bd6ffa.jpg?1783947450"
}

// =============================================================================
// Island (Cards 368-370)
// =============================================================================

val IceAgeIsland368 = basicLand("Island") {
    collectorNumber = "368"
    artist = "Anson Maddocks"
    imageUri = "https://cards.scryfall.io/normal/front/e/f/ef2d6fc9-ddad-4dd2-b218-afa1a5449b7e.jpg?1783947448"
}

val IceAgeIsland369 = basicLand("Island") {
    collectorNumber = "369"
    artist = "Anson Maddocks"
    imageUri = "https://cards.scryfall.io/normal/front/6/1/61a467ab-4460-4e5e-94c1-8150bfe0c954.jpg?1783947448"
}

val IceAgeIsland370 = basicLand("Island") {
    collectorNumber = "370"
    artist = "Anson Maddocks"
    imageUri = "https://cards.scryfall.io/normal/front/8/2/82f11c42-9d67-4833-9519-e165e6a7e9c4.jpg?1783947448"
}

// =============================================================================
// Swamp (Cards 373-375)
// =============================================================================

val IceAgeSwamp373 = basicLand("Swamp") {
    collectorNumber = "373"
    artist = "Douglas Shuler"
    imageUri = "https://cards.scryfall.io/normal/front/4/6/4695653a-5c4c-4ff3-b80c-f4b6c685f370.jpg?1783947448"
}

val IceAgeSwamp374 = basicLand("Swamp") {
    collectorNumber = "374"
    artist = "Douglas Shuler"
    imageUri = "https://cards.scryfall.io/normal/front/6/a/6a90b49f-53b3-4ce0-92c1-bcd76d6981ea.jpg?1783947447"
}

val IceAgeSwamp375 = basicLand("Swamp") {
    collectorNumber = "375"
    artist = "Douglas Shuler"
    imageUri = "https://cards.scryfall.io/normal/front/d/d/ddca7e2e-bb0a-47ed-ade3-31900da992dc.jpg?1783947447"
}

// =============================================================================
// Mountain (Cards 376-378)
// =============================================================================

val IceAgeMountain376 = basicLand("Mountain") {
    collectorNumber = "376"
    artist = "Tom Wänerstrand"
    imageUri = "https://cards.scryfall.io/normal/front/4/e/4ecf39c3-3b5f-4263-a7b5-9881bded3494.jpg?1783947446"
}

val IceAgeMountain377 = basicLand("Mountain") {
    collectorNumber = "377"
    artist = "Tom Wänerstrand"
    imageUri = "https://cards.scryfall.io/normal/front/2/e/2eb15b42-be2a-4663-b064-aad6c7cb2714.jpg?1783947447"
}

val IceAgeMountain378 = basicLand("Mountain") {
    collectorNumber = "378"
    artist = "Tom Wänerstrand"
    imageUri = "https://cards.scryfall.io/normal/front/1/7/17ac61e4-b543-4c37-9bfa-43f0c928152d.jpg?1783947446"
}

// =============================================================================
// Forest (Cards 380-382)
// =============================================================================

val IceAgeForest380 = basicLand("Forest") {
    collectorNumber = "380"
    artist = "Pat Lewis"
    imageUri = "https://cards.scryfall.io/normal/front/f/b/fbdcbd97-90a9-45ea-94f6-2a1c6faaf965.jpg?1783947446"
}

val IceAgeForest381 = basicLand("Forest") {
    collectorNumber = "381"
    artist = "Pat Lewis"
    imageUri = "https://cards.scryfall.io/normal/front/b/3/b346b784-7bde-49d0-bfa9-56236cbe19d9.jpg?1783947445"
}

val IceAgeForest382 = basicLand("Forest") {
    collectorNumber = "382"
    artist = "Pat Lewis"
    imageUri = "https://cards.scryfall.io/normal/front/7/6/768c4d8f-5700-4f0a-9ff2-58422aeb1dac.jpg?1783947444"
}

/**
 * All Ice Age basic land variants.
 */
val IceAgeBasicLands = listOf(
    IceAgePlains364, IceAgePlains365, IceAgePlains366,
    IceAgeIsland368, IceAgeIsland369, IceAgeIsland370,
    IceAgeSwamp373, IceAgeSwamp374, IceAgeSwamp375,
    IceAgeMountain376, IceAgeMountain377, IceAgeMountain378,
    IceAgeForest380, IceAgeForest381, IceAgeForest382
)
