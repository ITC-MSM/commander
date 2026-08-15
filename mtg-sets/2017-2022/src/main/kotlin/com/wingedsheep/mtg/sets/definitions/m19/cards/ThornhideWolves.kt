package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Thornhide Wolves reprint in M19.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * SOI's `cards/` package (the card's earliest real printing). This file
 * contributes only the M19-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ThornhideWolvesReprint = Printing(
    oracleId = "c5c221e6-dafe-4661-b920-3faed9551802",
    name = "Thornhide Wolves",
    setCode = "M19",
    collectorNumber = "204",
    artist = "Scott Murphy",
    imageUri = "https://cards.scryfall.io/normal/front/f/c/fc0f3812-bb6c-4d99-b505-9dfd84e3fd95.jpg?1783934527",
    releaseDate = "2018-07-13",
    rarity = Rarity.COMMON,
)
