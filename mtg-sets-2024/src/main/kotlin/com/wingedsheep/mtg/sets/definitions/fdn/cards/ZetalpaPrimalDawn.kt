package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Zetalpa, Primal Dawn reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ZetalpaPrimalDawnReprint = Printing(
    oracleId = "7da0e5de-3e4c-420a-8685-991206100b9d",
    name = "Zetalpa, Primal Dawn",
    setCode = "FDN",
    collectorNumber = "584",
    artist = "Chris Rallis",
    imageUri = "https://cards.scryfall.io/normal/front/6/9/694df80e-77d1-4455-b6f1-58d212267d76.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
