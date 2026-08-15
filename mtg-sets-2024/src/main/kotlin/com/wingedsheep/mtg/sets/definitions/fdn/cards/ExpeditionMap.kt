package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Expedition Map reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ZEN's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ExpeditionMapReprint = Printing(
    oracleId = "8fcf50cd-e6d0-4516-850f-d42ee75dcc3a",
    name = "Expedition Map",
    setCode = "FDN",
    collectorNumber = "724",
    artist = "Franz Vohwinkel",
    imageUri = "https://cards.scryfall.io/normal/front/0/8/08e66835-c228-48fa-bcaa-eb96edbd4f5a.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
