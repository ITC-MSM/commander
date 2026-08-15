package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Adamant Will reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AdamantWillReprint = Printing(
    oracleId = "467e22c3-6107-40ff-afc7-5960710c970b",
    name = "Adamant Will",
    setCode = "FDN",
    collectorNumber = "488",
    scryfallId = "1fd2c9ab-b3dd-4f68-b91f-0f075a045757",
    artist = "Irina Nordsol",
    imageUri = "https://cards.scryfall.io/normal/front/1/f/1fd2c9ab-b3dd-4f68-b91f-0f075a045757.jpg?1730490456",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
