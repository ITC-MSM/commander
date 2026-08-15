package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cathars' Crusade reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * AVR's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CatharsCrusadeReprint = Printing(
    oracleId = "cc65ac73-5bef-4ecb-ad8e-39199084c027",
    name = "Cathars' Crusade",
    setCode = "INR",
    collectorNumber = "17",
    artist = "Karl Kopinski",
    imageUri = "https://cards.scryfall.io/normal/front/5/2/5296e353-2efc-4d72-a877-7957eff630b9.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.RARE,
)
