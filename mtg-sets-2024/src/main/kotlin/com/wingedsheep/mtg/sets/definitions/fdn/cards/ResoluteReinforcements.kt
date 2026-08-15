package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Resolute Reinforcements reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DMU's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ResoluteReinforcementsReprint = Printing(
    oracleId = "eaaed253-9b11-47e2-8624-0340ab2207f6",
    name = "Resolute Reinforcements",
    setCode = "FDN",
    collectorNumber = "145",
    artist = "Billy Christian",
    imageUri = "https://cards.scryfall.io/normal/front/9/4/940f3989-77cc-49a9-92e0-095a75d80f0f.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
