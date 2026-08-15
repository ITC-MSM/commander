package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Adamant Will reprint in VOW.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the VOW-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AdamantWillReprint = Printing(
    oracleId = "467e22c3-6107-40ff-afc7-5960710c970b",
    name = "Adamant Will",
    setCode = "VOW",
    collectorNumber = "1",
    scryfallId = "bd091f3e-5fcc-4d12-b0c3-3b6340ab01d8",
    artist = "Irina Nordsol",
    imageUri = "https://cards.scryfall.io/normal/front/b/d/bd091f3e-5fcc-4d12-b0c3-3b6340ab01d8.jpg?1643585797",
    releaseDate = "2021-11-19",
    rarity = Rarity.COMMON,
)
