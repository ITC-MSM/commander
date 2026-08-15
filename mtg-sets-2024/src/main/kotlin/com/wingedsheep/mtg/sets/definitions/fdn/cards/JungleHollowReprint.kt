package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jungle Hollow reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val JungleHollowReprint = Printing(
    oracleId = "6de714e1-446d-4fb9-9e3d-bcd3ec6af9ca",
    name = "Jungle Hollow",
    setCode = "FDN",
    collectorNumber = "263",
    scryfallId = "dc758e14-d370-45e4-bbc5-938fb4d21127",
    artist = "Eytan Zana",
    imageUri = "https://cards.scryfall.io/normal/front/d/c/dc758e14-d370-45e4-bbc5-938fb4d21127.jpg?1730489583",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
