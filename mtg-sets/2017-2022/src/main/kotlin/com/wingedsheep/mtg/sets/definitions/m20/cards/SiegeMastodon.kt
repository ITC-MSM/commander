package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Siege Mastodon reprint in M20.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M20-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SiegeMastodonReprint = Printing(
    oracleId = "b4c404d8-9f2d-4429-ac36-449ae319abb7",
    name = "Siege Mastodon",
    setCode = "M20",
    collectorNumber = "312",
    artist = "Matt Cavotta",
    imageUri = "https://cards.scryfall.io/normal/front/7/1/71fd27a8-2de6-454f-8174-a60918bfe60e.jpg?1783932912",
    releaseDate = "2019-07-12",
    rarity = Rarity.COMMON,
)
