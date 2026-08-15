package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Basilisk Collar reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in
 * WWK's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BasiliskCollarReprint = Printing(
    oracleId = "f5f4dd28-f4ae-4d39-b9b8-6ebfd63c93fe",
    name = "Basilisk Collar",
    setCode = "FDN",
    collectorNumber = "669",
    scryfallId = "7b36fba7-71f7-4b7f-bde5-b3a9752ad21c",
    artist = "Craig J Spearing",
    imageUri = "https://cards.scryfall.io/normal/front/7/b/7b36fba7-71f7-4b7f-bde5-b3a9752ad21c.jpg?1730491131",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
