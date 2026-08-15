package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Markov Waltzer reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VOW's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MarkovWaltzerReprint = Printing(
    oracleId = "bc9ac394-15b6-4d95-acca-5dc247235d2b",
    name = "Markov Waltzer",
    setCode = "INR",
    collectorNumber = "245",
    artist = "Joshua Raphael",
    imageUri = "https://cards.scryfall.io/normal/front/2/3/231f4da2-8756-4f99-a46f-b8afe9adb80d.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.UNCOMMON,
)
