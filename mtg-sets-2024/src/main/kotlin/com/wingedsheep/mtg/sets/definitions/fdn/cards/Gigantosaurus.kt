package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gigantosaurus reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GigantosaurusReprint = Printing(
    oracleId = "e666bae7-dd51-4921-8b89-7e8d423caba0",
    name = "Gigantosaurus",
    setCode = "FDN",
    collectorNumber = "718",
    artist = "Loïc Canavaggia",
    imageUri = "https://cards.scryfall.io/normal/front/b/c/bc1c518a-dcb4-407c-85be-ed5935a24198.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
