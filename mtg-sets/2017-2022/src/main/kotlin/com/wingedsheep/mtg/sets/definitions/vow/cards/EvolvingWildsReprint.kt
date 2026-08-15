package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Evolving Wilds reprint in VOW.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the VOW-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EvolvingWildsReprint = Printing(
    oracleId = "a75445d3-1303-4bb5-89ad-26ea93fecd48",
    name = "Evolving Wilds",
    setCode = "VOW",
    collectorNumber = "263",
    scryfallId = "e80fe230-745d-42ae-a1f5-a8cc950783d0",
    artist = "Alayna Danner",
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e80fe230-745d-42ae-a1f5-a8cc950783d0.jpg?1643594919",
    releaseDate = "2021-11-19",
    rarity = Rarity.COMMON,
)
