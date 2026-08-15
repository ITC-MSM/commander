package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Evolving Wilds reprint in MID.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MID-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EvolvingWildsReprint = Printing(
    oracleId = "a75445d3-1303-4bb5-89ad-26ea93fecd48",
    name = "Evolving Wilds",
    setCode = "MID",
    collectorNumber = "261",
    scryfallId = "cb471f90-46f2-4037-87fc-f523fc9d004f",
    artist = "Alayna Danner",
    imageUri = "https://cards.scryfall.io/normal/front/c/b/cb471f90-46f2-4037-87fc-f523fc9d004f.jpg?1637114771",
    releaseDate = "2021-09-24",
    rarity = Rarity.COMMON,
)
