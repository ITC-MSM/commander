package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sporemound reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M14's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SporemoundReprint = Printing(
    oracleId = "1be56a3d-a6c0-4b65-ae71-3d90ceefc6c0",
    name = "Sporemound",
    setCode = "CMR",
    collectorNumber = "437",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/0/9/092bfc5f-8002-43da-8e70-c19fccfe54ac.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
