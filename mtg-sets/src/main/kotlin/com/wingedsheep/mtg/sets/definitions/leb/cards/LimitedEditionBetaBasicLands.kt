package com.wingedsheep.mtg.sets.definitions.leb.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Limited Edition Beta Basic Lands
 *
 * Beta printed three art variants of Island (cards 291-293), all by Mark Poole. The other four
 * basic land types aren't implemented for this set yet — [com.wingedsheep.mtg.sets.definitions.leb.BetaSet]
 * still falls back to Portal's Plains/Swamp/Mountain/Forest for those.
 */

val LebIsland291 = basicLand("Island") {
    collectorNumber = "291"
    artist = "Mark Poole"
    imageUri = "https://cards.scryfall.io/normal/front/b/f/bff33e91-8e52-43f2-b8ae-603b456b08fc.jpg?1783948594"
}

val LebIsland292 = basicLand("Island") {
    collectorNumber = "292"
    artist = "Mark Poole"
    imageUri = "https://cards.scryfall.io/normal/front/d/0/d0c5cf64-9844-4b5b-8e6b-b97c50cce053.jpg?1783948594"
}

val LebIsland293 = basicLand("Island") {
    collectorNumber = "293"
    artist = "Mark Poole"
    imageUri = "https://cards.scryfall.io/normal/front/c/0/c0a612c4-b4ac-4dd2-a06e-92516599fafd.jpg?1783948594"
}

val LimitedEditionBetaBasicLands = listOf(
    LebIsland291,
    LebIsland292,
    LebIsland293,
)
