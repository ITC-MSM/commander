package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Syncopate reprint in FIN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FIN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SyncopateReprint = Printing(
    oracleId = "318f7f70-e374-40ef-8afb-3389c10461d8",
    name = "Syncopate",
    setCode = "FIN",
    collectorNumber = "80",
    scryfallId = "83e1cd8d-5b97-427a-be75-2a1947f9c59b",
    artist = "Nijihayashi",
    imageUri = "https://cards.scryfall.io/normal/front/8/3/83e1cd8d-5b97-427a-be75-2a1947f9c59b.jpg?1748706062",
    releaseDate = "2025-06-13",
    rarity = Rarity.COMMON,
)
