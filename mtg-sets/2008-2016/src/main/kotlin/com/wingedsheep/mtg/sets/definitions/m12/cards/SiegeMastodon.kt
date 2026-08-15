package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Siege Mastodon reprint in M12.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M12-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SiegeMastodonReprint = Printing(
    oracleId = "b4c404d8-9f2d-4429-ac36-449ae319abb7",
    name = "Siege Mastodon",
    setCode = "M12",
    collectorNumber = "34",
    artist = "Matt Cavotta",
    imageUri = "https://cards.scryfall.io/normal/front/3/9/39c340a3-0118-48d3-99ab-f4a0e7099325.jpg?1783941098",
    releaseDate = "2011-07-15",
    rarity = Rarity.COMMON,
)
