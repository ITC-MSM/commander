package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dismal Backwater reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DismalBackwaterReprint = Printing(
    oracleId = "865a2194-fca0-446e-aae3-ca475cd66e00",
    name = "Dismal Backwater",
    setCode = "TDM",
    collectorNumber = "254",
    scryfallId = "082b52c9-c46e-44d3-b723-546ba528e07b",
    artist = "Alayna Danner",
    imageUri = "https://cards.scryfall.io/normal/front/0/8/082b52c9-c46e-44d3-b723-546ba528e07b.jpg?1743697562",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
