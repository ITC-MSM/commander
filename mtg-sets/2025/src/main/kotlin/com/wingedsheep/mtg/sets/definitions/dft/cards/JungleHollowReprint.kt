package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jungle Hollow reprint in DFT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DFT-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val JungleHollowReprint = Printing(
    oracleId = "6de714e1-446d-4fb9-9e3d-bcd3ec6af9ca",
    name = "Jungle Hollow",
    setCode = "DFT",
    collectorNumber = "256",
    scryfallId = "7dd24a1a-08db-4fa7-8939-f5541677327e",
    artist = "Eddie Mendoza",
    imageUri = "https://cards.scryfall.io/normal/front/7/d/7dd24a1a-08db-4fa7-8939-f5541677327e.jpg?1738356909",
    releaseDate = "2025-02-14",
    rarity = Rarity.COMMON,
)
