package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dune Beetle reprint in J22.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * AKH's `cards/` package (the card's earliest real printing). This file
 * contributes only the J22-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DuneBeetleReprint = Printing(
    oracleId = "3f79603a-3173-404e-8c69-b2254c1270dc",
    name = "Dune Beetle",
    setCode = "J22",
    collectorNumber = "407",
    artist = "Grzegorz Rutkowski",
    imageUri = "https://cards.scryfall.io/normal/front/3/b/3bd4f7da-5200-4c1b-8c42-95d601952995.jpg?1783919009",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
