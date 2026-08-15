package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Siege Mastodon reprint in M14.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M14-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SiegeMastodonReprint = Printing(
    oracleId = "b4c404d8-9f2d-4429-ac36-449ae319abb7",
    name = "Siege Mastodon",
    setCode = "M14",
    collectorNumber = "34",
    artist = "Matt Cavotta",
    imageUri = "https://cards.scryfall.io/normal/front/4/0/40e7a30f-bb29-4c6b-bf70-53e9e4292814.jpg?1783939940",
    releaseDate = "2013-07-19",
    rarity = Rarity.COMMON,
)
