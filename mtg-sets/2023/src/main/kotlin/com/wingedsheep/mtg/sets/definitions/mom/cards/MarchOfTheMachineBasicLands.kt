package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * March of the Machine Basic Lands
 *
 * MOM prints three art variants of each basic land type: one full-set cycle by Sam Burley
 * (277-281), then two more variants per type (282-291).
 */

// =============================================================================
// Plains (277, 282, 283)
// =============================================================================

val MarchOfTheMachinePlains277 = basicLand("Plains") {
    collectorNumber = "277"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/0/0/00bf269d-aca3-494c-8777-de90ae903af2.jpg?1783916929"
}

val MarchOfTheMachinePlains282 = basicLand("Plains") {
    collectorNumber = "282"
    artist = "Jorge Jacinto"
    imageUri = "https://cards.scryfall.io/normal/front/e/e/ee7f525c-d777-4adf-8920-8adfc73bbc55.jpg?1783916926"
}

val MarchOfTheMachinePlains283 = basicLand("Plains") {
    collectorNumber = "283"
    artist = "Lucas Staniec"
    imageUri = "https://cards.scryfall.io/normal/front/8/a/8a790328-70b3-477d-bf02-8718870367ae.jpg?1783916925"
}

// =============================================================================
// Island (278, 284, 285)
// =============================================================================

val MarchOfTheMachineIsland278 = basicLand("Island") {
    collectorNumber = "278"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/9/8/988bfa94-0c83-4057-a0c7-0ad885b8919c.jpg?1783916928"
}

val MarchOfTheMachineIsland284 = basicLand("Island") {
    collectorNumber = "284"
    artist = "Grady Frederick"
    imageUri = "https://cards.scryfall.io/normal/front/2/0/2039d727-3bb6-4a76-89ca-159ecf10cad8.jpg?1783916925"
}

val MarchOfTheMachineIsland285 = basicLand("Island") {
    collectorNumber = "285"
    artist = "Henry Peters"
    imageUri = "https://cards.scryfall.io/normal/front/5/6/560c7de9-0046-4a76-a41a-fa1c3ef92f04.jpg?1783916926"
}

// =============================================================================
// Swamp (279, 286, 287)
// =============================================================================

val MarchOfTheMachineSwamp279 = basicLand("Swamp") {
    collectorNumber = "279"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/8/8/888cdb76-d365-41df-8b6a-378ac58ca8b2.jpg?1783916927"
}

val MarchOfTheMachineSwamp286 = basicLand("Swamp") {
    collectorNumber = "286"
    artist = "Raymond Bonilla"
    imageUri = "https://cards.scryfall.io/normal/front/e/1/e19e24a8-5a4f-4a2d-b8dd-eb92aac7be78.jpg?1783916920"
}

val MarchOfTheMachineSwamp287 = basicLand("Swamp") {
    collectorNumber = "287"
    artist = "Julian Kok Joon Wen"
    imageUri = "https://cards.scryfall.io/normal/front/0/1/013612b4-fed9-4112-8018-9267ae608fd7.jpg?1783916921"
}

// =============================================================================
// Mountain (280, 288, 289)
// =============================================================================

val MarchOfTheMachineMountain280 = basicLand("Mountain") {
    collectorNumber = "280"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/2/1/21041ed6-8f13-4909-a2c3-fa4894a2d1e3.jpg?1783916928"
}

val MarchOfTheMachineMountain288 = basicLand("Mountain") {
    collectorNumber = "288"
    artist = "Jorge Jacinto"
    imageUri = "https://cards.scryfall.io/normal/front/a/6/a6a57c6a-3603-466c-8a81-a9eb8de92aec.jpg?1783916920"
}

val MarchOfTheMachineMountain289 = basicLand("Mountain") {
    collectorNumber = "289"
    artist = "Lucas Staniec"
    imageUri = "https://cards.scryfall.io/normal/front/7/e/7e19c7e1-1b4b-4c7e-b011-eff8ed7de0fd.jpg?1783916920"
}

// =============================================================================
// Forest (281, 290, 291)
// =============================================================================

val MarchOfTheMachineForest281 = basicLand("Forest") {
    collectorNumber = "281"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/8/0/80716ed1-8d0e-44e6-8b18-606e80d22181.jpg?1783916927"
}

val MarchOfTheMachineForest290 = basicLand("Forest") {
    collectorNumber = "290"
    artist = "Grady Frederick"
    imageUri = "https://cards.scryfall.io/normal/front/7/e/7e6151d4-5129-4631-84a1-5cffc551c1e9.jpg?1783916919"
}

val MarchOfTheMachineForest291 = basicLand("Forest") {
    collectorNumber = "291"
    artist = "Henry Peters"
    imageUri = "https://cards.scryfall.io/normal/front/e/0/e0ce5575-2d62-43c9-9c4b-fca4aff6ae4d.jpg?1783916919"
}

/**
 * All March of the Machine basic land variants.
 */
val MarchOfTheMachineBasicLands = listOf(
    MarchOfTheMachinePlains277, MarchOfTheMachinePlains282, MarchOfTheMachinePlains283,
    MarchOfTheMachineIsland278, MarchOfTheMachineIsland284, MarchOfTheMachineIsland285,
    MarchOfTheMachineSwamp279, MarchOfTheMachineSwamp286, MarchOfTheMachineSwamp287,
    MarchOfTheMachineMountain280, MarchOfTheMachineMountain288, MarchOfTheMachineMountain289,
    MarchOfTheMachineForest281, MarchOfTheMachineForest290, MarchOfTheMachineForest291
)
