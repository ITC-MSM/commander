package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Phyrexia: All Will Be One Basic Lands
 *
 * ONE prints two booster basics per type — the standard art (262-266) and Mark Riddick's
 * "compleated" art (267-271) — plus one non-booster full-art variant per type (272-276).
 * The non-booster variants are kept defined for collection/display but excluded from the
 * draft/sealed basic pool.
 */

// =============================================================================
// Plains (Cards 262, 267, 272)
// =============================================================================

val OnePlains262 = basicLand("Plains") {
    collectorNumber = "262"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/5/0/50958c6e-9555-42ad-9bb3-2da9faa5cb52.jpg?1783917978"
}

val OnePlains267 = basicLand("Plains") {
    collectorNumber = "267"
    artist = "Mark Riddick"
    imageUri = "https://cards.scryfall.io/normal/front/3/6/36ccde39-98bd-4a67-bfcf-a66d9fbd9417.jpg?1783917976"
}

val OnePlains272 = basicLand("Plains") {
    collectorNumber = "272"
    artist = "Sergey Glushakov"
    imageUri = "https://cards.scryfall.io/normal/front/d/b/db14da86-6721-4e22-9b61-4a5680d4e5a3.jpg?1783917975"
    inBooster = false
}

// =============================================================================
// Island (Cards 263, 268, 273)
// =============================================================================

val OneIsland263 = basicLand("Island") {
    collectorNumber = "263"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/5/c/5c6322d4-52bf-471a-a5b2-8329ef4be39a.jpg?1783917977"
}

val OneIsland268 = basicLand("Island") {
    collectorNumber = "268"
    artist = "Mark Riddick"
    imageUri = "https://cards.scryfall.io/normal/front/a/c/ac97baa8-5e57-45b6-9c64-cb9ce4806294.jpg?1783917975"
}

val OneIsland273 = basicLand("Island") {
    collectorNumber = "273"
    artist = "David Álvarez"
    imageUri = "https://cards.scryfall.io/normal/front/f/a/fa641d46-d002-4903-af72-e96971f558bc.jpg?1783917974"
    inBooster = false
}

// =============================================================================
// Swamp (Cards 264, 269, 274)
// =============================================================================

val OneSwamp264 = basicLand("Swamp") {
    collectorNumber = "264"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/7/3/736a0484-e091-4178-92c5-c517b0e92f3d.jpg?1783917977"
}

val OneSwamp269 = basicLand("Swamp") {
    collectorNumber = "269"
    artist = "Mark Riddick"
    imageUri = "https://cards.scryfall.io/normal/front/1/8/187e9fa1-6a50-4697-8645-fea4524e8dde.jpg?1783917974"
}

val OneSwamp274 = basicLand("Swamp") {
    collectorNumber = "274"
    artist = "Julian Kok Joon Wen"
    imageUri = "https://cards.scryfall.io/normal/front/d/4/d485c620-f1fb-4715-b7c5-d2d56588308d.jpg?1783917973"
    inBooster = false
}

// =============================================================================
// Mountain (Cards 265, 270, 275)
// =============================================================================

val OneMountain265 = basicLand("Mountain") {
    collectorNumber = "265"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/6/d/6d9e44c1-7b51-47cb-b564-608deb46cc44.jpg?1783917976"
}

val OneMountain270 = basicLand("Mountain") {
    collectorNumber = "270"
    artist = "Mark Riddick"
    imageUri = "https://cards.scryfall.io/normal/front/5/5/55c61b29-797f-4fda-acf1-039a807954c9.jpg?1783917974"
}

val OneMountain275 = basicLand("Mountain") {
    collectorNumber = "275"
    artist = "Muhammad Firdaus"
    imageUri = "https://cards.scryfall.io/normal/front/c/d/cd95f833-27ce-447c-b505-02137daaba4c.jpg?1783917973"
    inBooster = false
}

// =============================================================================
// Forest (Cards 266, 271, 276)
// =============================================================================

val OneForest266 = basicLand("Forest") {
    collectorNumber = "266"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/4/f/4fce7045-4572-4e9e-8853-2a5dcfc989ac.jpg?1783917975"
}

val OneForest271 = basicLand("Forest") {
    collectorNumber = "271"
    artist = "Mark Riddick"
    imageUri = "https://cards.scryfall.io/normal/front/b/6/b6ec4ded-7c8d-416a-9a12-3e5d6c91ac93.jpg?1783917973"
}

val OneForest276 = basicLand("Forest") {
    collectorNumber = "276"
    artist = "Nadia Hurianova"
    imageUri = "https://cards.scryfall.io/normal/front/9/f/9f9e911e-7e12-4d99-806c-5cfba19ea8f3.jpg?1783917974"
    inBooster = false
}

/**
 * All Phyrexia: All Will Be One basic land variants.
 */
val PhyrexiaAllWillBeOneBasicLands = listOf(
    OnePlains262, OnePlains267, OnePlains272,
    OneIsland263, OneIsland268, OneIsland273,
    OneSwamp264, OneSwamp269, OneSwamp274,
    OneMountain265, OneMountain270, OneMountain275,
    OneForest266, OneForest271, OneForest276,
)
