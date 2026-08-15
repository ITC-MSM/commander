package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Noxious Dragon reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * FRF's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val NoxiousDragonReprint = Printing(
    oracleId = "fe75b364-9e65-499d-9763-76844cf88cf7",
    name = "Noxious Dragon",
    setCode = "CMR",
    collectorNumber = "139",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/0/1/01afdb32-d9a8-42d4-9ad2-4608ffd661f0.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
