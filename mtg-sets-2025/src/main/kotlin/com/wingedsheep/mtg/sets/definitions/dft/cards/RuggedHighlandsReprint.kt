package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rugged Highlands reprint in DFT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DFT-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RuggedHighlandsReprint = Printing(
    oracleId = "6c922206-6e68-4dcd-9559-88da1074f2c4",
    name = "Rugged Highlands",
    setCode = "DFT",
    collectorNumber = "262",
    scryfallId = "73b7484f-923c-46c5-95bf-c2c6706c0d48",
    artist = "Florian de Gesincourt",
    imageUri = "https://cards.scryfall.io/normal/front/7/3/73b7484f-923c-46c5-95bf-c2c6706c0d48.jpg?1738356938",
    releaseDate = "2025-02-14",
    rarity = Rarity.COMMON,
)
