package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Brushland reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BrushlandReprint = Printing(
    oracleId = "5eb8b497-ec9a-4a89-ad29-1ec3ca82da7c",
    name = "Brushland",
    setCode = "BLC",
    collectorNumber = "295",
    scryfallId = "0f04a729-2e7e-459b-83ca-7d57b6b13ac3",
    artist = "Thomas Stoop",
    imageUri = "https://cards.scryfall.io/normal/front/0/f/0f04a729-2e7e-459b-83ca-7d57b6b13ac3.jpg?1721429685",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
