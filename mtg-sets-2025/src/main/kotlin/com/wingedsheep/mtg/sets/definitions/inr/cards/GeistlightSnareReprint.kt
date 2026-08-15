package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Geistlight Snare reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VOW's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GeistlightSnareReprint = Printing(
    oracleId = "45f159dc-a5f9-4335-896b-ad544c303801",
    name = "Geistlight Snare",
    setCode = "INR",
    collectorNumber = "66",
    artist = "Anato Finnstark",
    imageUri = "https://cards.scryfall.io/normal/front/2/9/293511f6-09c2-4311-a2a6-0b0b7ef65bef.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.UNCOMMON,
)
