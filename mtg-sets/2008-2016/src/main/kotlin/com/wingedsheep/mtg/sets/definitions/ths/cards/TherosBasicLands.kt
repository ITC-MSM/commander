package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Theros Basic Lands
 *
 * Theros contains 4 art variants of each basic land type.
 * Cards 230-249 (Plains 230-233, Island 234-237, Swamp 238-241, Mountain 242-245, Forest 246-249)
 */

// =============================================================================
// Plains (Cards 230-233)
// =============================================================================

val TherosPlains230 = basicLand("Plains") {
    collectorNumber = "230"
    artist = "Rob Alexander"
    imageUri = "https://cards.scryfall.io/normal/front/1/8/18013a78-d156-42bf-89b7-72e7070872c0.jpg"
}

val TherosPlains231 = basicLand("Plains") {
    collectorNumber = "231"
    artist = "Steven Belledin"
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e891d622-9369-4eeb-b7e2-183c60ec54d2.jpg"
}

val TherosPlains232 = basicLand("Plains") {
    collectorNumber = "232"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/2/f/2fa4b03f-60f1-441f-8066-8bc13d5637d1.jpg"
}

val TherosPlains233 = basicLand("Plains") {
    collectorNumber = "233"
    artist = "Raoul Vitale"
    imageUri = "https://cards.scryfall.io/normal/front/f/3/f37db921-ab2a-46db-82c1-1f1abf7a3cf7.jpg"
}

// =============================================================================
// Island (Cards 234-237)
// =============================================================================

val TherosIsland234 = basicLand("Island") {
    collectorNumber = "234"
    artist = "Rob Alexander"
    imageUri = "https://cards.scryfall.io/normal/front/d/8/d851b88b-6ce6-4889-83c2-e191307bcee6.jpg"
}

val TherosIsland235 = basicLand("Island") {
    collectorNumber = "235"
    artist = "Steven Belledin"
    imageUri = "https://cards.scryfall.io/normal/front/a/c/ac928336-f534-4682-a952-c536a1b14e1e.jpg"
}

val TherosIsland236 = basicLand("Island") {
    collectorNumber = "236"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/e/9/e9c266e3-d403-434a-b645-dcb9cf441680.jpg"
}

val TherosIsland237 = basicLand("Island") {
    collectorNumber = "237"
    artist = "Raoul Vitale"
    imageUri = "https://cards.scryfall.io/normal/front/5/1/51852e9d-9183-4f9a-a724-cb60cf677e38.jpg"
}

// =============================================================================
// Swamp (Cards 238-241)
// =============================================================================

val TherosSwamp238 = basicLand("Swamp") {
    collectorNumber = "238"
    artist = "Rob Alexander"
    imageUri = "https://cards.scryfall.io/normal/front/8/b/8b153bb8-f9d9-44dc-ae19-d0eb2ea26ba1.jpg"
}

val TherosSwamp239 = basicLand("Swamp") {
    collectorNumber = "239"
    artist = "Steven Belledin"
    imageUri = "https://cards.scryfall.io/normal/front/b/f/bfc9f472-c410-4595-b08d-87c115b9148d.jpg"
}

val TherosSwamp240 = basicLand("Swamp") {
    collectorNumber = "240"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/e/4/e49a9a01-46fb-4e9d-8e7d-5f2c267ab1f9.jpg"
}

val TherosSwamp241 = basicLand("Swamp") {
    collectorNumber = "241"
    artist = "Raoul Vitale"
    imageUri = "https://cards.scryfall.io/normal/front/8/1/81047292-e537-47b5-acfa-ed839707109c.jpg"
}

// =============================================================================
// Mountain (Cards 242-245)
// =============================================================================

val TherosMountain242 = basicLand("Mountain") {
    collectorNumber = "242"
    artist = "Rob Alexander"
    imageUri = "https://cards.scryfall.io/normal/front/e/7/e78801a7-af8c-4f1d-b29e-7a36676b6818.jpg"
}

val TherosMountain243 = basicLand("Mountain") {
    collectorNumber = "243"
    artist = "Steven Belledin"
    imageUri = "https://cards.scryfall.io/normal/front/f/c/fc0a5a9c-e59a-4e33-b584-d6daa7f81547.jpg"
}

val TherosMountain244 = basicLand("Mountain") {
    collectorNumber = "244"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/3/a/3a7245d4-7a29-4423-8b44-5e9694e1d9a1.jpg"
}

val TherosMountain245 = basicLand("Mountain") {
    collectorNumber = "245"
    artist = "Raoul Vitale"
    imageUri = "https://cards.scryfall.io/normal/front/2/9/29557b92-fded-4aa8-8db7-99168c8e70d8.jpg"
}

// =============================================================================
// Forest (Cards 246-249)
// =============================================================================

val TherosForest246 = basicLand("Forest") {
    collectorNumber = "246"
    artist = "Rob Alexander"
    imageUri = "https://cards.scryfall.io/normal/front/d/d/ddfd1469-94b9-4964-ada6-4fb43b0b9282.jpg"
}

val TherosForest247 = basicLand("Forest") {
    collectorNumber = "247"
    artist = "Steven Belledin"
    imageUri = "https://cards.scryfall.io/normal/front/b/3/b3a37f57-5d3e-488e-a909-f53bd4c2576b.jpg"
}

val TherosForest248 = basicLand("Forest") {
    collectorNumber = "248"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/f/1/f13b34d7-e2a6-4cbc-9d5f-b6fe05a1c701.jpg"
}

val TherosForest249 = basicLand("Forest") {
    collectorNumber = "249"
    artist = "Raoul Vitale"
    imageUri = "https://cards.scryfall.io/normal/front/7/a/7a80d346-84e3-4eaf-9635-09da472475d8.jpg"
}

/**
 * All Theros basic land variants.
 */
val TherosBasicLands = listOf(
    TherosPlains230, TherosPlains231, TherosPlains232, TherosPlains233,
    TherosIsland234, TherosIsland235, TherosIsland236, TherosIsland237,
    TherosSwamp238, TherosSwamp239, TherosSwamp240, TherosSwamp241,
    TherosMountain242, TherosMountain243, TherosMountain244, TherosMountain245,
    TherosForest246, TherosForest247, TherosForest248, TherosForest249
)
