package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Dragons of Tarkir Basic Lands
 *
 * DTK prints three art variants of each basic land type (250-264).
 */

// =============================================================================
// Plains (250, 251, 252)
// =============================================================================

val DragonsOfTarkirPlains250 = basicLand("Plains") {
    collectorNumber = "250"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/c/d/cdf91075-8d8d-43c3-8125-2469e2dcd132.jpg?1783938566"
}

val DragonsOfTarkirPlains251 = basicLand("Plains") {
    collectorNumber = "251"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/2/4/2484c9db-44bc-411c-af43-bfe0e30f0ffa.jpg?1783938566"
}

val DragonsOfTarkirPlains252 = basicLand("Plains") {
    collectorNumber = "252"
    artist = "Florian de Gesincourt"
    imageUri = "https://cards.scryfall.io/normal/front/9/1/910f2bcd-8d37-417c-a969-86e04f1daf23.jpg?1783938566"
}

// =============================================================================
// Island (253, 254, 255)
// =============================================================================

val DragonsOfTarkirIsland253 = basicLand("Island") {
    collectorNumber = "253"
    artist = "Florian de Gesincourt"
    imageUri = "https://cards.scryfall.io/normal/front/f/8/f8c2f901-7d7b-47ec-a3d4-96ddcbd9218c.jpg?1783938565"
}

val DragonsOfTarkirIsland254 = basicLand("Island") {
    collectorNumber = "254"
    artist = "Florian de Gesincourt"
    imageUri = "https://cards.scryfall.io/normal/front/7/8/78732e7f-421d-43e8-8f43-341bb01e993e.jpg?1783938565"
}

val DragonsOfTarkirIsland255 = basicLand("Island") {
    collectorNumber = "255"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/9/6/966ac118-6d94-4f3b-9873-7a2440ab92d9.jpg?1783938565"
}

// =============================================================================
// Swamp (256, 257, 258)
// =============================================================================

val DragonsOfTarkirSwamp256 = basicLand("Swamp") {
    collectorNumber = "256"
    artist = "Noah Bradley"
    imageUri = "https://cards.scryfall.io/normal/front/c/7/c7f4498e-e753-418e-8db6-e11d2caac3b1.jpg?1783938564"
}

val DragonsOfTarkirSwamp257 = basicLand("Swamp") {
    collectorNumber = "257"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/6/d/6d4873cb-43ce-47b4-9d91-a8495a244c85.jpg?1783938564"
}

val DragonsOfTarkirSwamp258 = basicLand("Swamp") {
    collectorNumber = "258"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/0/d/0d846729-deec-436f-af5c-08faf53ec36f.jpg?1783938564"
}

// =============================================================================
// Mountain (259, 260, 261)
// =============================================================================

val DragonsOfTarkirMountain259 = basicLand("Mountain") {
    collectorNumber = "259"
    artist = "Noah Bradley"
    imageUri = "https://cards.scryfall.io/normal/front/5/4/54968fa2-36f1-4d21-89e7-a307d68cfccc.jpg?1783938565"
}

val DragonsOfTarkirMountain260 = basicLand("Mountain") {
    collectorNumber = "260"
    artist = "Noah Bradley"
    imageUri = "https://cards.scryfall.io/normal/front/d/7/d7f613b2-de5b-4592-8d14-5ad373537a21.jpg?1783938564"
}

val DragonsOfTarkirMountain261 = basicLand("Mountain") {
    collectorNumber = "261"
    artist = "Titus Lunter"
    imageUri = "https://cards.scryfall.io/normal/front/5/c/5c356eb7-ebf4-400d-95a7-7452382bc32f.jpg?1783938564"
}

// =============================================================================
// Forest (262, 263, 264)
// =============================================================================

val DragonsOfTarkirForest262 = basicLand("Forest") {
    collectorNumber = "262"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/0/f/0f341b39-ac40-436b-967c-568265354886.jpg?1783938563"
}

val DragonsOfTarkirForest263 = basicLand("Forest") {
    collectorNumber = "263"
    artist = "Titus Lunter"
    imageUri = "https://cards.scryfall.io/normal/front/2/3/23c9425a-2093-40c1-b3a9-a882d80cf198.jpg?1783938564"
}

val DragonsOfTarkirForest264 = basicLand("Forest") {
    collectorNumber = "264"
    artist = "Titus Lunter"
    imageUri = "https://cards.scryfall.io/normal/front/6/7/67becef5-cd70-4fe9-b8a3-1bff2ea04ab4.jpg?1783938563"
}


/**
 * All Dragons of Tarkir basic land variants.
 */
val DragonsOfTarkirBasicLands = listOf(
    DragonsOfTarkirPlains250, DragonsOfTarkirPlains251, DragonsOfTarkirPlains252,
    DragonsOfTarkirIsland253, DragonsOfTarkirIsland254, DragonsOfTarkirIsland255,
    DragonsOfTarkirSwamp256, DragonsOfTarkirSwamp257, DragonsOfTarkirSwamp258,
    DragonsOfTarkirMountain259, DragonsOfTarkirMountain260, DragonsOfTarkirMountain261,
    DragonsOfTarkirForest262, DragonsOfTarkirForest263, DragonsOfTarkirForest264
)
