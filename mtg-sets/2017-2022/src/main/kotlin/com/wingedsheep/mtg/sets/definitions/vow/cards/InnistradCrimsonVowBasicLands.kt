package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Innistrad: Crimson Vow Basic Lands
 *
 * VOW prints two full-art booster basics per type inside the main set numbering (268-277) and one
 * regular-frame bundle art per type (398-402), plus the non-booster "moonlit land" WPN promos
 * (408-412). The promos are kept defined for collection/display but excluded from the draft/sealed
 * basic pool — a limited deck is played with the set's standard art, 268/270/272/274/276.
 */

// =============================================================================
// Plains (Cards 268, 269, 398, 408)
// =============================================================================

val VowPlains268 = basicLand("Plains") {
    collectorNumber = "268"
    artist = "Daria Khlebnikova"
    imageUri = "https://cards.scryfall.io/normal/front/d/e/deabdaa1-6227-48e4-82d5-63a1771320b2.jpg?1783924781"
}

val VowPlains269 = basicLand("Plains") {
    collectorNumber = "269"
    artist = "Indra Nugroho"
    imageUri = "https://cards.scryfall.io/normal/front/2/b/2b069f97-735a-4d85-8504-b5a863bd659b.jpg?1783924780"
}

val VowPlains398 = basicLand("Plains") {
    collectorNumber = "398"
    artist = "Sam White"
    imageUri = "https://cards.scryfall.io/normal/front/2/0/20ddb0be-d62d-46fa-b753-36dfab935e8a.jpg?1783924716"
}

val VowPlains408 = basicLand("Plains") {
    collectorNumber = "408"
    artist = "Daria Khlebnikova"
    imageUri = "https://cards.scryfall.io/normal/front/8/0/80897666-ade2-4f70-9c4d-235753b44a23.jpg?1782702899"
    inBooster = false
}

// =============================================================================
// Island (Cards 270, 271, 399, 409)
// =============================================================================

val VowIsland270 = basicLand("Island") {
    collectorNumber = "270"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/5/4/54ddd3aa-593c-4adb-b591-33c15d02131c.jpg?1783924780"
}

val VowIsland271 = basicLand("Island") {
    collectorNumber = "271"
    artist = "Rio Krisma"
    imageUri = "https://cards.scryfall.io/normal/front/5/4/54591ec7-94a1-470c-927a-788b6a514444.jpg?1783924780"
}

val VowIsland399 = basicLand("Island") {
    collectorNumber = "399"
    artist = "Sam White"
    imageUri = "https://cards.scryfall.io/normal/front/f/8/f82b281f-d3c7-4eb8-9a10-b4808ca6cfcd.jpg?1783924715"
}

val VowIsland409 = basicLand("Island") {
    collectorNumber = "409"
    artist = "Rio Krisma"
    imageUri = "https://cards.scryfall.io/normal/front/1/f/1fddf4cb-0680-4bc7-8bb3-cb15268aff46.jpg?1782702899"
    inBooster = false
}

// =============================================================================
// Swamp (Cards 272, 273, 400, 410)
// =============================================================================

val VowSwamp272 = basicLand("Swamp") {
    collectorNumber = "272"
    artist = "Pig Hands"
    imageUri = "https://cards.scryfall.io/normal/front/4/a/4abfe418-15f8-46ce-9b39-fd5a38b25d12.jpg?1783924779"
}

val VowSwamp273 = basicLand("Swamp") {
    collectorNumber = "273"
    artist = "Kerby Rosanes"
    imageUri = "https://cards.scryfall.io/normal/front/2/e/2e55a405-bf5b-4158-ba9a-239627ac9701.jpg?1783924779"
}

val VowSwamp400 = basicLand("Swamp") {
    collectorNumber = "400"
    artist = "Sam White"
    imageUri = "https://cards.scryfall.io/normal/front/b/e/be87de91-aa10-4ed8-83a2-261b4f57e7db.jpg?1783924715"
}

val VowSwamp410 = basicLand("Swamp") {
    collectorNumber = "410"
    artist = "Kerby Rosanes"
    imageUri = "https://cards.scryfall.io/normal/front/9/1/91afb4f0-70ef-4539-9081-dc130c7c63f5.jpg?1782702899"
    inBooster = false
}

// =============================================================================
// Mountain (Cards 274, 275, 401, 411)
// =============================================================================

val VowMountain274 = basicLand("Mountain") {
    collectorNumber = "274"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/8/a/8a4448b6-0dbe-427c-b145-8ac915fc0dfc.jpg?1783924779"
}

val VowMountain275 = basicLand("Mountain") {
    collectorNumber = "275"
    artist = "Daria Khlebnikova"
    imageUri = "https://cards.scryfall.io/normal/front/a/6/a6f72e53-52bb-4cf4-9b8b-34ed0c5f7c3c.jpg?1783924779"
}

val VowMountain401 = basicLand("Mountain") {
    collectorNumber = "401"
    artist = "Sam White"
    imageUri = "https://cards.scryfall.io/normal/front/b/8/b86fbf78-57ec-4a0f-9fb6-e4bf13861563.jpg?1783924714"
}

val VowMountain411 = basicLand("Mountain") {
    collectorNumber = "411"
    artist = "Daria Khlebnikova"
    imageUri = "https://cards.scryfall.io/normal/front/1/1/117716bf-43c8-4534-92da-d7948d4b5628.jpg?1782702898"
    inBooster = false
}

// =============================================================================
// Forest (Cards 276, 277, 402, 412)
// =============================================================================

val VowForest276 = basicLand("Forest") {
    collectorNumber = "276"
    artist = "Pig Hands"
    imageUri = "https://cards.scryfall.io/normal/front/e/4/e4c83b60-3d49-4fdc-a6b7-06d1a0c4a126.jpg?1783924777"
}

val VowForest277 = basicLand("Forest") {
    collectorNumber = "277"
    artist = "Indra Nugroho"
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e8760175-e9bb-4ef9-87ca-591d1edd5163.jpg?1783924776"
}

val VowForest402 = basicLand("Forest") {
    collectorNumber = "402"
    artist = "Sam White"
    imageUri = "https://cards.scryfall.io/normal/front/7/a/7ae6380e-b1b6-4a5a-a8d9-7cdf8eab3557.jpg?1783924713"
}

val VowForest412 = basicLand("Forest") {
    collectorNumber = "412"
    artist = "Pig Hands"
    imageUri = "https://cards.scryfall.io/normal/front/a/7/a7279281-42d5-4226-b841-f1f4deff919b.jpg?1782702898"
    inBooster = false
}

/**
 * All Innistrad: Crimson Vow basic land variants.
 */
val InnistradCrimsonVowBasicLands = listOf(
    VowPlains268, VowPlains269, VowPlains398, VowPlains408,
    VowIsland270, VowIsland271, VowIsland399, VowIsland409,
    VowSwamp272, VowSwamp273, VowSwamp400, VowSwamp410,
    VowMountain274, VowMountain275, VowMountain401, VowMountain411,
    VowForest276, VowForest277, VowForest402, VowForest412,
)
