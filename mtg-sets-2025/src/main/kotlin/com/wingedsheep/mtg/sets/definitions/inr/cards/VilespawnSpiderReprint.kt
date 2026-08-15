package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vilespawn Spider reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VOW's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VilespawnSpiderReprint = Printing(
    oracleId = "3e969073-76cc-4a2c-9abd-3a4094488fe5",
    name = "Vilespawn Spider",
    setCode = "INR",
    collectorNumber = "251",
    artist = "Nicholas Gregory",
    imageUri = "https://cards.scryfall.io/normal/front/5/8/58901777-f8bf-450b-acf8-d60a73c3322f.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.UNCOMMON,
)
