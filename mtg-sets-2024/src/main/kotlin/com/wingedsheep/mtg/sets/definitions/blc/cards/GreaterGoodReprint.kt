package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Greater Good reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * Urza's Saga's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GreaterGoodReprint = Printing(
    oracleId = "dc0593c2-ccb4-4648-a592-c5bcd121dc72",
    name = "Greater Good",
    setCode = "BLC",
    collectorNumber = "223",
    scryfallId = "debe9258-209a-44cb-99e9-49affd02aa32",
    artist = "Mathias Kollros",
    imageUri = "https://cards.scryfall.io/normal/front/d/e/debe9258-209a-44cb-99e9-49affd02aa32.jpg?1721429302",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
