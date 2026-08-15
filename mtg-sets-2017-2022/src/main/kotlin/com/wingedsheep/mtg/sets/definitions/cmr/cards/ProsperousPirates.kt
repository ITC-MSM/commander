package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Prosperous Pirates reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * XLN's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ProsperousPiratesReprint = Printing(
    oracleId = "9dc6281b-08e0-4c66-9aef-abc7184ca36a",
    name = "Prosperous Pirates",
    setCode = "CMR",
    collectorNumber = "85",
    artist = "Josh Hass",
    imageUri = "https://cards.scryfall.io/normal/front/5/d/5d778bde-f927-419e-8052-6230562bdedd.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
