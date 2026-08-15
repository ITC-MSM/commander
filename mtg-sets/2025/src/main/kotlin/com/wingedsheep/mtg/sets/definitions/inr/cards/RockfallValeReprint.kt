package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rockfall Vale reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MID's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RockfallValeReprint = Printing(
    oracleId = "185c70c1-8403-4ae5-b45d-3679d4ee092a",
    name = "Rockfall Vale",
    setCode = "INR",
    collectorNumber = "282",
    artist = "Muhammad Firdaus",
    imageUri = "https://cards.scryfall.io/normal/front/d/4/d4ef22f2-7c4f-4198-982b-f0830fd769cc.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.RARE,
)
