package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Corsair Captain reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * JMP's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CorsairCaptainReprint = Printing(
    oracleId = "a7ec13c6-7ade-433a-b5a2-047854eef486",
    name = "Corsair Captain",
    setCode = "FDN",
    collectorNumber = "506",
    artist = "Victor Adame Minguez",
    imageUri = "https://cards.scryfall.io/normal/front/d/5/d5017dbc-07fd-45ea-9629-7c584144e8be.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
