package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Siege Mastodon reprint in M11.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M11-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SiegeMastodonReprint = Printing(
    oracleId = "b4c404d8-9f2d-4429-ac36-449ae319abb7",
    name = "Siege Mastodon",
    setCode = "M11",
    collectorNumber = "29",
    artist = "Matt Cavotta",
    imageUri = "https://cards.scryfall.io/normal/front/2/9/296d7ce0-866a-43eb-939e-3287ff00234d.jpg?1783941831",
    releaseDate = "2010-07-16",
    rarity = Rarity.COMMON,
)
