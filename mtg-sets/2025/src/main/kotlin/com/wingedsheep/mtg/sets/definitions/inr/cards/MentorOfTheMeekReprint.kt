package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mentor of the Meek reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ISD's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MentorOfTheMeekReprint = Printing(
    oracleId = "b9f4f96b-6e54-4fe6-8df7-623e0fc72409",
    name = "Mentor of the Meek",
    setCode = "INR",
    collectorNumber = "34",
    artist = "Jana Schirmer & Johannes Voss",
    imageUri = "https://cards.scryfall.io/normal/front/2/8/280ca0f4-be07-452a-bbc0-c25570f14008.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.UNCOMMON,
)
