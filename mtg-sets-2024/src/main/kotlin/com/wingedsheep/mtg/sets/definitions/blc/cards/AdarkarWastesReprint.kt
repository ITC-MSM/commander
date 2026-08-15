package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Adarkar Wastes reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AdarkarWastesReprint = Printing(
    oracleId = "d5ad26cc-2bdb-46b7-b8bf-dd099d5fa09b",
    name = "Adarkar Wastes",
    setCode = "BLC",
    collectorNumber = "291",
    scryfallId = "d501527c-1bf5-4b59-9bff-e4562181a096",
    artist = "Piotr Dura",
    imageUri = "https://cards.scryfall.io/normal/front/d/5/d501527c-1bf5-4b59-9bff-e4562181a096.jpg?1721429664",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
