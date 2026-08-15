package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Ravnica: City of Guilds Basic Lands
 *
 * Ravnica contains 4 art variants of each basic land type.
 * Cards 287-306 (Plains 287-290, Island 291-294, Swamp 295-298, Mountain 299-302, Forest 303-306)
 */

// =============================================================================
// Plains (Cards 287-290)
// =============================================================================

val Plains287 = basicLand("Plains") {
    collectorNumber = "287"
    artist = "Stephan Martiniere"
    imageUri = "https://cards.scryfall.io/normal/front/3/d/3d8d3cd4-0f5f-4424-82ee-d8ba81da47fd.jpg"
}

val Plains288 = basicLand("Plains") {
    collectorNumber = "288"
    artist = "Christopher Moeller"
    imageUri = "https://cards.scryfall.io/normal/front/9/7/97b7beb9-0805-47b9-8e62-ff110e9e086a.jpg"
}

val Plains289 = basicLand("Plains") {
    collectorNumber = "289"
    artist = "Anthony S. Waters"
    imageUri = "https://cards.scryfall.io/normal/front/7/a/7ab3d0fe-6af6-4d02-b41d-0b88b510be4f.jpg"
}

val Plains290 = basicLand("Plains") {
    collectorNumber = "290"
    artist = "Richard Wright"
    imageUri = "https://cards.scryfall.io/normal/front/7/c/7c2839fa-33eb-47fb-9541-77c68612aa02.jpg"
}

// =============================================================================
// Island (Cards 291-294)
// =============================================================================

val Island291 = basicLand("Island") {
    collectorNumber = "291"
    artist = "Stephan Martiniere"
    imageUri = "https://cards.scryfall.io/normal/front/d/b/db6c8056-f155-434c-a4cb-a532a4707245.jpg"
}

val Island292 = basicLand("Island") {
    collectorNumber = "292"
    artist = "Christopher Moeller"
    imageUri = "https://cards.scryfall.io/normal/front/1/d/1d603978-3877-454e-8910-53a4cc95431d.jpg"
}

val Island293 = basicLand("Island") {
    collectorNumber = "293"
    artist = "Anthony S. Waters"
    imageUri = "https://cards.scryfall.io/normal/front/4/2/42a272ca-98a7-4887-b6b7-903a1adc33e0.jpg"
}

val Island294 = basicLand("Island") {
    collectorNumber = "294"
    artist = "Richard Wright"
    imageUri = "https://cards.scryfall.io/normal/front/f/9/f9cba2b8-d865-46e8-81f1-752598a0d729.jpg"
}

// =============================================================================
// Swamp (Cards 295-298)
// =============================================================================

val Swamp295 = basicLand("Swamp") {
    collectorNumber = "295"
    artist = "Stephan Martiniere"
    imageUri = "https://cards.scryfall.io/normal/front/2/7/2755f9f7-95f9-4907-8ec9-21853eb376f4.jpg"
}

val Swamp296 = basicLand("Swamp") {
    collectorNumber = "296"
    artist = "Christopher Moeller"
    imageUri = "https://cards.scryfall.io/normal/front/f/1/f18dd43d-0d9e-4d61-90f9-63822cf0cb2d.jpg"
}

val Swamp297 = basicLand("Swamp") {
    collectorNumber = "297"
    artist = "Anthony S. Waters"
    imageUri = "https://cards.scryfall.io/normal/front/f/b/fbd659b0-1f79-4e3b-bc20-8f384aa43670.jpg"
}

val Swamp298 = basicLand("Swamp") {
    collectorNumber = "298"
    artist = "Richard Wright"
    imageUri = "https://cards.scryfall.io/normal/front/0/d/0d1a1df0-e6dc-4815-bb73-9d100ee6e0b2.jpg"
}

// =============================================================================
// Mountain (Cards 299-302)
// =============================================================================

val Mountain299 = basicLand("Mountain") {
    collectorNumber = "299"
    artist = "Stephan Martiniere"
    imageUri = "https://cards.scryfall.io/normal/front/d/a/dad3662a-f7b4-45d5-a35e-fc6c38474692.jpg"
}

val Mountain300 = basicLand("Mountain") {
    collectorNumber = "300"
    artist = "Christopher Moeller"
    imageUri = "https://cards.scryfall.io/normal/front/6/4/6477ebad-e1e5-4508-aa20-39b47e39a5c6.jpg"
}

val Mountain301 = basicLand("Mountain") {
    collectorNumber = "301"
    artist = "Anthony S. Waters"
    imageUri = "https://cards.scryfall.io/normal/front/4/f/4f312d7b-f505-45c3-bb87-650259e5db50.jpg"
}

val Mountain302 = basicLand("Mountain") {
    collectorNumber = "302"
    artist = "Richard Wright"
    imageUri = "https://cards.scryfall.io/normal/front/e/1/e135c9f9-5099-4029-bc19-dd9a0760d86f.jpg"
}

// =============================================================================
// Forest (Cards 303-306)
// =============================================================================

val Forest303 = basicLand("Forest") {
    collectorNumber = "303"
    artist = "Stephan Martiniere"
    imageUri = "https://cards.scryfall.io/normal/front/7/0/706be1bd-720d-4e74-b71f-568081afcab1.jpg"
}

val Forest304 = basicLand("Forest") {
    collectorNumber = "304"
    artist = "Christopher Moeller"
    imageUri = "https://cards.scryfall.io/normal/front/8/3/839f63ad-476a-4344-b6c3-911c1075c2b8.jpg"
}

val Forest305 = basicLand("Forest") {
    collectorNumber = "305"
    artist = "Anthony S. Waters"
    imageUri = "https://cards.scryfall.io/normal/front/f/e/fe2e6db4-94a4-4fa9-be52-1ec235d2b137.jpg"
}

val Forest306 = basicLand("Forest") {
    collectorNumber = "306"
    artist = "Richard Wright"
    imageUri = "https://cards.scryfall.io/normal/front/a/d/ade29c12-43f6-473d-bcc8-1e3f2cf4ee8f.jpg"
}

/**
 * All Ravnica: City of Guilds basic land variants.
 */
val RavnicaBasicLands = listOf(
    Plains287, Plains288, Plains289, Plains290,
    Island291, Island292, Island293, Island294,
    Swamp295, Swamp296, Swamp297, Swamp298,
    Mountain299, Mountain300, Mountain301, Mountain302,
    Forest303, Forest304, Forest305, Forest306
)
