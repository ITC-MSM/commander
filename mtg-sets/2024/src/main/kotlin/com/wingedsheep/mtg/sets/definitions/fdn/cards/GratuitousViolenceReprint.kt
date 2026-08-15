package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gratuitous Violence reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GratuitousViolenceReprint = Printing(
    oracleId = "7c340a39-4ee0-4ba1-bb66-6674f8020fda",
    name = "Gratuitous Violence",
    setCode = "FDN",
    collectorNumber = "715",
    scryfallId = "e89024c5-eef5-468c-92b4-e53dd212909c",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e89024c5-eef5-468c-92b4-e53dd212909c.jpg?1730572714",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
