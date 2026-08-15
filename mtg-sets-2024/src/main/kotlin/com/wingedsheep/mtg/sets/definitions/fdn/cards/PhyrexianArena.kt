package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Phyrexian Arena reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * APC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PhyrexianArenaReprint = Printing(
    oracleId = "ee579a32-a048-4335-b966-231ba731cdea",
    name = "Phyrexian Arena",
    setCode = "FDN",
    collectorNumber = "180",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/0/7/0784b6f0-9ebf-43d2-ba0f-a6bc93ba0c48.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
