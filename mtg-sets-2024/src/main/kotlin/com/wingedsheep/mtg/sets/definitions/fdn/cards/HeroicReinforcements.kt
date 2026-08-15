package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Heroic Reinforcements reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HeroicReinforcementsReprint = Printing(
    oracleId = "c442e8b0-a0a4-4839-8049-125b91b15c99",
    name = "Heroic Reinforcements",
    setCode = "FDN",
    collectorNumber = "241",
    artist = "Scott Murphy",
    imageUri = "https://cards.scryfall.io/normal/front/6/a/6a05e8d5-c2ad-489a-888d-22622886b620.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
