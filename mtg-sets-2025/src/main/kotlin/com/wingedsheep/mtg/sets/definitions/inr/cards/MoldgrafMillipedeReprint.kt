package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Moldgraf Millipede reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VOW's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MoldgrafMillipedeReprint = Printing(
    oracleId = "48edac92-0dd8-4274-bf1b-122243770540",
    name = "Moldgraf Millipede",
    setCode = "INR",
    collectorNumber = "208",
    artist = "Simon Dominic",
    imageUri = "https://cards.scryfall.io/normal/front/3/a/3a163ec7-4c9a-4d45-965c-e5ddeb07a3e0.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.COMMON,
)
