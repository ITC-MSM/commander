package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Evolving Wilds reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EvolvingWildsReprint = Printing(
    oracleId = "a75445d3-1303-4bb5-89ad-26ea93fecd48",
    name = "Evolving Wilds",
    setCode = "TDM",
    collectorNumber = "255",
    scryfallId = "62209251-4118-4843-895b-46afb7284c75",
    artist = "Leon Tukker",
    imageUri = "https://cards.scryfall.io/normal/front/6/2/62209251-4118-4843-895b-46afb7284c75.jpg?1743205007",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
