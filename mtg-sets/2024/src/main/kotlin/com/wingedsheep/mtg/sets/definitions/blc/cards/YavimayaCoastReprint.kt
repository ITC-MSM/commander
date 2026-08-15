package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Yavimaya Coast reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val YavimayaCoastReprint = Printing(
    oracleId = "40b36bc6-c185-4bda-99e7-0118953c2c97",
    name = "Yavimaya Coast",
    setCode = "BLC",
    collectorNumber = "355",
    scryfallId = "797b266b-4403-4ba4-9ffd-9c62fd981153",
    artist = "Anthony S. Waters",
    imageUri = "https://cards.scryfall.io/normal/front/7/9/797b266b-4403-4ba4-9ffd-9c62fd981153.jpg?1721429958",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
