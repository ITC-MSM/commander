package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Counterspell reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CounterspellReprint = Printing(
    oracleId = "cc187110-1148-4090-bbb8-e205694a39f5",
    name = "Counterspell",
    setCode = "CMR",
    collectorNumber = "395",
    artist = "Zack Stella",
    imageUri = "https://cards.scryfall.io/normal/front/c/e/ce30f926-bc06-46ee-9f35-0cdf09a67043.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
