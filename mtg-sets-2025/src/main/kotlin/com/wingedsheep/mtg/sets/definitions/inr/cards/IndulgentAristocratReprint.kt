package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Indulgent Aristocrat reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * SOI's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val IndulgentAristocratReprint = Printing(
    oracleId = "c7054a2d-2e7b-4487-8fc3-f6a47a716fd3",
    name = "Indulgent Aristocrat",
    setCode = "INR",
    collectorNumber = "118",
    artist = "Anna Steinbauer",
    imageUri = "https://cards.scryfall.io/normal/front/0/7/07015524-874f-4856-a5c1-3148bd126886.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.UNCOMMON,
)
