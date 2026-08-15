package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Hinterland Harbor reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HinterlandHarborReprint = Printing(
    oracleId = "fb5a3403-7f0b-406c-8c4f-d693be010ca6",
    name = "Hinterland Harbor",
    setCode = "BLC",
    collectorNumber = "312",
    scryfallId = "1fa8b7e9-e6c7-4b04-aab4-2a9edf5b4320",
    artist = "Daniel Ljunggren",
    imageUri = "https://cards.scryfall.io/normal/front/1/f/1fa8b7e9-e6c7-4b04-aab4-2a9edf5b4320.jpg?1721429757",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
