package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Snakeskin Veil reprint in OTJ.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the OTJ-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SnakeskinVeilReprint = Printing(
    oracleId = "1e6a24be-8281-41c1-a5ba-b68f0ef1d7b8",
    name = "Snakeskin Veil",
    setCode = "OTJ",
    collectorNumber = "181",
    scryfallId = "133fbdec-0d00-433f-9015-5eb091126e3a",
    artist = "Dan Murayama Scott",
    imageUri = "https://cards.scryfall.io/normal/front/1/3/133fbdec-0d00-433f-9015-5eb091126e3a.jpg?1712355994",
    releaseDate = "2024-04-19",
    rarity = Rarity.COMMON,
)
