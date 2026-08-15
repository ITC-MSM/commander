package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Orazca Frillback reprint in JMP.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file
 * contributes only the JMP-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OrazcaFrillbackReprint = Printing(
    oracleId = "2da32ec3-f42f-4edc-b5b3-e5f39fcf370c",
    name = "Orazca Frillback",
    setCode = "JMP",
    collectorNumber = "416",
    artist = "Simon Dominic",
    imageUri = "https://cards.scryfall.io/normal/front/2/0/20471a3b-90f9-4463-9b43-fc7b9b28f5d1.jpg?1783930358",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
