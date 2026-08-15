package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Voldaren Duelist reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * SOI's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VoldarenDuelistReprint = Printing(
    oracleId = "a96d0496-babc-4774-9b72-bea9d2b22743",
    name = "Voldaren Duelist",
    setCode = "INR",
    collectorNumber = "181",
    artist = "Jason Rainville",
    imageUri = "https://cards.scryfall.io/normal/front/7/e/7e2df669-a996-4692-9c04-7ba236595076.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.COMMON,
)
