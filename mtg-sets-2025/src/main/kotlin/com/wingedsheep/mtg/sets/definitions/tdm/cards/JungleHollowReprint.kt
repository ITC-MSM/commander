package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jungle Hollow reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val JungleHollowReprint = Printing(
    oracleId = "6de714e1-446d-4fb9-9e3d-bcd3ec6af9ca",
    name = "Jungle Hollow",
    setCode = "TDM",
    collectorNumber = "258",
    scryfallId = "ea13440b-3f7b-4182-9541-27c1fa3121e5",
    artist = "Cristi Balanescu",
    imageUri = "https://cards.scryfall.io/normal/front/e/a/ea13440b-3f7b-4182-9541-27c1fa3121e5.jpg?1743205019",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
