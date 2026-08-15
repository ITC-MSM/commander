package com.wingedsheep.mtg.sets.definitions.mir.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fog reprint in MIR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the MIR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FogReprint = Printing(
    oracleId = "27e9db49-7af7-4bef-ad4c-bf5dfb92030d",
    name = "Fog",
    setCode = "MIR",
    collectorNumber = "216",
    artist = "Harold McNeill",
    imageUri = "https://cards.scryfall.io/normal/front/6/d/6d822598-2f0f-45fa-9643-0368e2c0e18b.jpg",
    releaseDate = "1996-10-08",
    rarity = Rarity.COMMON,
)
