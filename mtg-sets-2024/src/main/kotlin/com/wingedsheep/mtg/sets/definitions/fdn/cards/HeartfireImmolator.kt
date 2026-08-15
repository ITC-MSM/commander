package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Heartfire Immolator reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M21's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HeartfireImmolatorReprint = Printing(
    oracleId = "1e414a4f-6e76-4cbe-9c69-d52852cd5bc0",
    name = "Heartfire Immolator",
    setCode = "FDN",
    collectorNumber = "201",
    artist = "Donato Giancola",
    imageUri = "https://cards.scryfall.io/normal/front/3/c/3ca38f4d-01f5-4a02-9000-01261a440dbf.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
