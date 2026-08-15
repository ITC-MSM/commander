package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Snapping Drake reprint in RAV.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * POR's `cards/` package (the card's earliest real printing). This file contributes only
 * the RAV-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SnappingDrakeReprint = Printing(
    oracleId = "e15060c3-3773-4548-8747-ff59dcf2b519",
    name = "Snapping Drake",
    setCode = "RAV",
    collectorNumber = "64",
    artist = "Dave Dorman",
    imageUri = "https://cards.scryfall.io/normal/front/c/d/cd1e21fa-4d85-464b-9e64-3ba284769df9.jpg",
    releaseDate = "2005-10-07",
    rarity = Rarity.COMMON,
)
