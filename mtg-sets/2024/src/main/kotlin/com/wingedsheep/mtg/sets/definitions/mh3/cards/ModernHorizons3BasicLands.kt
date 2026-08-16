package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Modern Horizons 3 Basic Lands
 *
 * MH3 prints one full-art booster basic per type (304-308) plus two non-booster art variants
 * per type (310-319). The non-booster variants are kept defined for collection/display but
 * excluded from the draft/sealed basic pool.
 *
 * Snow-Covered Wastes is MH3's sixth basic land. It carries the Snow supertype, which the
 * `basicLand` helper cannot express, so it lives in its own file as a hand-written `card`.
 */

// =============================================================================
// Plains (Cards 304, 310, 311)
// =============================================================================

val Mh3Plains304 = basicLand("Plains") {
    collectorNumber = "304"
    artist = "Nadia Hurianova"
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e81ecd4f-4cde-4d8f-a9b7-d7c6098be981.jpg?1783911206"
}

val Mh3Plains310 = basicLand("Plains") {
    collectorNumber = "310"
    artist = "Volkan Baǵa"
    imageUri = "https://cards.scryfall.io/normal/front/e/0/e0281fba-d771-4431-931f-920db2f14c47.jpg?1783911204"
    inBooster = false
}

val Mh3Plains311 = basicLand("Plains") {
    collectorNumber = "311"
    artist = "Lius Lasahido"
    imageUri = "https://cards.scryfall.io/normal/front/d/3/d3bd857b-87d0-4a0f-9aac-0b1b661bb3bd.jpg?1783911204"
    inBooster = false
}

// =============================================================================
// Island (Cards 305, 312, 313)
// =============================================================================

val Mh3Island305 = basicLand("Island") {
    collectorNumber = "305"
    artist = "Samuele Bandini"
    imageUri = "https://cards.scryfall.io/normal/front/1/c/1cb1ac28-ee04-4892-97ea-2cfdebbafcad.jpg?1783911207"
}

val Mh3Island312 = basicLand("Island") {
    collectorNumber = "312"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/6/4/64daf0ac-678b-4683-9351-a6daf9c9f849.jpg?1783911203"
    inBooster = false
}

val Mh3Island313 = basicLand("Island") {
    collectorNumber = "313"
    artist = "Raph Lomotan"
    imageUri = "https://cards.scryfall.io/normal/front/d/4/d4f54250-74a9-4f77-bf60-bf13585c1a1b.jpg?1783911203"
    inBooster = false
}

// =============================================================================
// Swamp (Cards 306, 314, 315)
// =============================================================================

val Mh3Swamp306 = basicLand("Swamp") {
    collectorNumber = "306"
    artist = "Yohann Schepacz"
    imageUri = "https://cards.scryfall.io/normal/front/c/8/c8e3909e-e00a-4855-a0be-b1c538f89cb8.jpg?1783911206"
}

val Mh3Swamp314 = basicLand("Swamp") {
    collectorNumber = "314"
    artist = "David Álvarez"
    imageUri = "https://cards.scryfall.io/normal/front/f/5/f5e672fc-673b-4476-b9cc-a395dc60b471.jpg?1783911202"
    inBooster = false
}

val Mh3Swamp315 = basicLand("Swamp") {
    collectorNumber = "315"
    artist = "Carlos Palma Cruchaga"
    imageUri = "https://cards.scryfall.io/normal/front/5/3/537fb023-4352-406f-8435-51b37b99a6be.jpg?1783911202"
    inBooster = false
}

// =============================================================================
// Mountain (Cards 307, 316, 317)
// =============================================================================

val Mh3Mountain307 = basicLand("Mountain") {
    collectorNumber = "307"
    artist = "Chuck Lukacs"
    imageUri = "https://cards.scryfall.io/normal/front/e/9/e9744bb5-9ad1-4287-9929-1146377d975b.jpg?1783911204"
}

val Mh3Mountain316 = basicLand("Mountain") {
    collectorNumber = "316"
    artist = "Carlos Palma Cruchaga"
    imageUri = "https://cards.scryfall.io/normal/front/1/8/186e36d5-3f1d-4423-a8db-f4c38898fa30.jpg?1783911201"
    inBooster = false
}

val Mh3Mountain317 = basicLand("Mountain") {
    collectorNumber = "317"
    artist = "Paolo Parente"
    imageUri = "https://cards.scryfall.io/normal/front/1/3/13b0928b-0469-4e3a-a8c8-1df26abb6707.jpg?1783911202"
    inBooster = false
}

// =============================================================================
// Forest (Cards 308, 318, 319)
// =============================================================================

val Mh3Forest308 = basicLand("Forest") {
    collectorNumber = "308"
    artist = "Vance Kovacs"
    imageUri = "https://cards.scryfall.io/normal/front/f/f/ff4c78b4-7178-4a60-ba22-086fb18146df.jpg?1783911204"
}

val Mh3Forest318 = basicLand("Forest") {
    collectorNumber = "318"
    artist = "Lars Grant-West"
    imageUri = "https://cards.scryfall.io/normal/front/7/a/7ac34881-de32-42c7-af60-f992638e1da2.jpg?1783911201"
    inBooster = false
}

val Mh3Forest319 = basicLand("Forest") {
    collectorNumber = "319"
    artist = "Brian Valeza"
    imageUri = "https://cards.scryfall.io/normal/front/6/5/6527c8ce-4a63-45f2-ae8e-c651e8713716.jpg?1783911201"
    inBooster = false
}

/**
 * All Modern Horizons 3 basic land variants.
 */
val ModernHorizons3BasicLands = listOf(
    Mh3Plains304, Mh3Plains310, Mh3Plains311,
    Mh3Island305, Mh3Island312, Mh3Island313,
    Mh3Swamp306, Mh3Swamp314, Mh3Swamp315,
    Mh3Mountain307, Mh3Mountain316, Mh3Mountain317,
    Mh3Forest308, Mh3Forest318, Mh3Forest319,
)
