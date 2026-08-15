package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Nebelgast Herald reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * EMN's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val NebelgastHeraldReprint = Printing(
    oracleId = "cf336e4c-a0d3-43aa-ad23-b98effb2b751",
    name = "Nebelgast Herald",
    setCode = "INR",
    collectorNumber = "78",
    artist = "Zezhou Chen",
    imageUri = "https://cards.scryfall.io/normal/front/5/1/51173564-cd0e-410f-82fc-3d071e6f4d17.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.UNCOMMON,
)
