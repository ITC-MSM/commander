package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gratuitous Violence reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GratuitousViolenceReprint = Printing(
    oracleId = "7c340a39-4ee0-4ba1-bb66-6674f8020fda",
    name = "Gratuitous Violence",
    setCode = "BLC",
    collectorNumber = "197",
    scryfallId = "bab5985d-636e-41e3-8138-3e108eef8221",
    artist = "Christopher Moeller",
    imageUri = "https://cards.scryfall.io/normal/front/b/a/bab5985d-636e-41e3-8138-3e108eef8221.jpg?1721429159",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
