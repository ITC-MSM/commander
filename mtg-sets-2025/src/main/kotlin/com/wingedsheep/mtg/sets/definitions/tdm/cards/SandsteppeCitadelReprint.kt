package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sandsteppe Citadel reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SandsteppeCitadelReprint = Printing(
    oracleId = "544dbabd-cbfc-40da-a5ba-2fea9cddb453",
    name = "Sandsteppe Citadel",
    setCode = "TDM",
    collectorNumber = "266",
    scryfallId = "47f47e7f-39ba-4807-8e32-7262a61dfbba",
    artist = "Diego Gisbert",
    imageUri = "https://cards.scryfall.io/normal/front/4/7/47f47e7f-39ba-4807-8e32-7262a61dfbba.jpg?1743205050",
    releaseDate = "2025-04-11",
    rarity = Rarity.UNCOMMON,
)
