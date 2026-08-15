package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tempest Djinn reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TempestDjinnReprint = Printing(
    oracleId = "b94c233e-11d3-4556-a1da-bd193e58b663",
    name = "Tempest Djinn",
    setCode = "FDN",
    collectorNumber = "749",
    scryfallId = "3de55b97-059c-4d7b-afd0-44fd8380df4c",
    artist = "Zezhou Chen",
    imageUri = "https://cards.scryfall.io/normal/front/3/d/3de55b97-059c-4d7b-afd0-44fd8380df4c.jpg?1775599816",
    releaseDate = "2026-04-24",
    rarity = Rarity.RARE,
)
