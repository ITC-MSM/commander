package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Evolving Wilds reprint in WOE.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the WOE-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EvolvingWildsReprint = Printing(
    oracleId = "a75445d3-1303-4bb5-89ad-26ea93fecd48",
    name = "Evolving Wilds",
    setCode = "WOE",
    collectorNumber = "256",
    scryfallId = "74f9c819-719a-461b-8e7e-a26c88e8099b",
    artist = "Alayna Danner",
    imageUri = "https://cards.scryfall.io/normal/front/7/4/74f9c819-719a-461b-8e7e-a26c88e8099b.jpg?1692939977",
    releaseDate = "2023-09-08",
    rarity = Rarity.COMMON,
)
