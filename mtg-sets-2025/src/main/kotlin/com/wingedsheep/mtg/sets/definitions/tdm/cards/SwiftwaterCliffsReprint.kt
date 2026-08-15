package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swiftwater Cliffs reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SwiftwaterCliffsReprint = Printing(
    oracleId = "2f4ad084-2062-44c0-9975-15f100204531",
    name = "Swiftwater Cliffs",
    setCode = "TDM",
    collectorNumber = "268",
    scryfallId = "ca53fb19-b8ca-485b-af1a-5117ae54bfe3",
    artist = "Piotr Dura",
    imageUri = "https://cards.scryfall.io/normal/front/c/a/ca53fb19-b8ca-485b-af1a-5117ae54bfe3.jpg?1743205059",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
