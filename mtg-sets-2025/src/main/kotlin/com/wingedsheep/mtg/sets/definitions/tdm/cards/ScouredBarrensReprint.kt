package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Scoured Barrens reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ScouredBarrensReprint = Printing(
    oracleId = "d37f858e-03c8-4594-9b92-cd03699a1591",
    name = "Scoured Barrens",
    setCode = "TDM",
    collectorNumber = "267",
    scryfallId = "b4b47b80-69ed-44b0-afa0-ca90206dc16d",
    artist = "Brent Hollowell",
    imageUri = "https://cards.scryfall.io/normal/front/b/4/b4b47b80-69ed-44b0-afa0-ca90206dc16d.jpg?1743205056",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
