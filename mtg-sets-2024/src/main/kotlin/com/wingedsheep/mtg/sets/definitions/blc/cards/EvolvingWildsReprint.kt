package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Evolving Wilds reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EvolvingWildsReprint = Printing(
    oracleId = "a75445d3-1303-4bb5-89ad-26ea93fecd48",
    name = "Evolving Wilds",
    setCode = "BLC",
    collectorNumber = "302",
    scryfallId = "516d6669-929e-4884-ae92-9a98cb7f9376",
    artist = "Alayna Danner",
    imageUri = "https://cards.scryfall.io/normal/front/5/1/516d6669-929e-4884-ae92-9a98cb7f9376.jpg?1721429716",
    releaseDate = "2024-08-02",
    rarity = Rarity.COMMON,
)
