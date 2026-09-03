package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * War of the Spark Basic Lands
 *
 * One art per basic land type, cards 250/253/256/259/262 (the set prints several arts per
 * type; these are the first of each).
 *
 * A basic land is **not** a `Printing` row: `basicLand(...)` builds a full per-set
 * `CardDefinition`, which the set's `basicLands` override discovers through
 * `CardDiscovery.findBasicLandsIn`. (`check-card-printing` flags every basic land in every set
 * as printing drift for exactly this reason — that class of report is a script limitation, not
 * a gap here.)
 */

val WarOfTheSparkPlains250 = basicLand("Plains") {
    collectorNumber = "250"
    artist = "Jonas De Ro"
    imageUri = "https://cards.scryfall.io/normal/front/d/9/d92ef517-2417-43a2-8b1a-0673d1531c65.jpg"
}

val WarOfTheSparkIsland253 = basicLand("Island") {
    collectorNumber = "253"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/7/0/7014b9fc-a906-4ffd-a482-22ba8dbe3b4a.jpg"
}

val WarOfTheSparkSwamp256 = basicLand("Swamp") {
    collectorNumber = "256"
    artist = "Titus Lunter"
    imageUri = "https://cards.scryfall.io/normal/front/2/4/24eeb424-235d-4346-9355-57914e740ec6.jpg"
}

val WarOfTheSparkMountain259 = basicLand("Mountain") {
    collectorNumber = "259"
    artist = "Titus Lunter"
    imageUri = "https://cards.scryfall.io/normal/front/4/8/489fdba7-5c25-4cf3-a1e0-3e0fda6c6ee6.jpg"
}

val WarOfTheSparkForest262 = basicLand("Forest") {
    collectorNumber = "262"
    artist = "Jonas De Ro"
    imageUri = "https://cards.scryfall.io/normal/front/a/9/a9d61651-349e-40d0-a7c4-c9561e190405.jpg"
}

/**
 * All War of the Spark basic land variants.
 */
val WarOfTheSparkBasicLands = listOf(
    WarOfTheSparkPlains250,
    WarOfTheSparkIsland253,
    WarOfTheSparkSwamp256,
    WarOfTheSparkMountain259,
    WarOfTheSparkForest262
)
