package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Festerhide Boar reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ISD's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FesterhideBoarReprint = Printing(
    oracleId = "be1fc2cf-00b8-424f-b683-3743fba285c9",
    name = "Festerhide Boar",
    setCode = "INR",
    collectorNumber = "196",
    artist = "Nils Hamm",
    imageUri = "https://cards.scryfall.io/normal/front/b/4/b401dba6-c26e-45c1-b10a-12116fb1cb4e.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.COMMON,
)
