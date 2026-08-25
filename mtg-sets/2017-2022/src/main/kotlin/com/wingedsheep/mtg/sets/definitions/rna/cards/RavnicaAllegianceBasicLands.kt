package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Ravnica Allegiance Basic Lands
 *
 * One art per basic land type, cards 260-264.
 *
 * A basic land is **not** a `Printing` row: `basicLand(...)` builds a full per-set
 * `CardDefinition`, which the set's `basicLands` override discovers through
 * `CardDiscovery.findBasicLandsIn`. (`check-card-printing` flags every basic land in every set
 * as printing drift for exactly this reason — that class of report is a script limitation, not
 * a gap here.)
 */

val RavnicaAllegiancePlains260 = basicLand("Plains") {
    collectorNumber = "260"
    artist = "Titus Lunter"
    imageUri = "https://cards.scryfall.io/normal/front/9/4/9433619d-5bd1-41e9-ab7a-364c98347b1d.jpg"
}

val RavnicaAllegianceIsland261 = basicLand("Island") {
    collectorNumber = "261"
    artist = "Eytan Zana"
    imageUri = "https://cards.scryfall.io/normal/front/1/9/197f5bd0-5ab3-4bf4-b20e-1389c0e9527a.jpg"
}

val RavnicaAllegianceSwamp262 = basicLand("Swamp") {
    collectorNumber = "262"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/3/0/308809ad-c150-49b1-83e3-b78494156d7a.jpg"
}

val RavnicaAllegianceMountain263 = basicLand("Mountain") {
    collectorNumber = "263"
    artist = "Jonas De Ro"
    imageUri = "https://cards.scryfall.io/normal/front/5/4/54a773e3-93f0-4bf8-ab6a-8cee939d743a.jpg"
}

val RavnicaAllegianceForest264 = basicLand("Forest") {
    collectorNumber = "264"
    artist = "Eytan Zana"
    imageUri = "https://cards.scryfall.io/normal/front/4/8/48764854-d268-462d-a016-27329c8f062d.jpg"
}

/**
 * All Ravnica Allegiance basic land variants.
 */
val RavnicaAllegianceBasicLands = listOf(
    RavnicaAllegiancePlains260,
    RavnicaAllegianceIsland261,
    RavnicaAllegianceSwamp262,
    RavnicaAllegianceMountain263,
    RavnicaAllegianceForest264
)
