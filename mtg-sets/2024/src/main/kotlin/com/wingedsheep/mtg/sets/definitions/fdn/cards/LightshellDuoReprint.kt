package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Lightshell Duo reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val LightshellDuoReprint = Printing(
    oracleId = "ef8fab12-d7d2-4c21-8b8c-41627048ac8b",
    name = "Lightshell Duo",
    setCode = "FDN",
    collectorNumber = "157",
    scryfallId = "bb75315c-ea8f-4eb0-899e-c73ef75fc396",
    artist = "Mariah Tekulve",
    imageUri = "https://cards.scryfall.io/normal/front/b/b/bb75315c-ea8f-4eb0-899e-c73ef75fc396.jpg?1730489189",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
