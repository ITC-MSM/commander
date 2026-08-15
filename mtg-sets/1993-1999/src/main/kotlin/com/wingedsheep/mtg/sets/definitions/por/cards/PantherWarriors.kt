package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Panther Warriors reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PantherWarriorsReprint = Printing(
    oracleId = "ef7788af-8edc-46df-a5b6-895c734ea423",
    name = "Panther Warriors",
    setCode = "POR",
    collectorNumber = "180",
    artist = "Eric Peterson",
    imageUri = "https://cards.scryfall.io/normal/front/8/b/8be610ce-5b84-416e-b427-98887642ff01.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
