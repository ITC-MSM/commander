package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Stone Rain reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StoneRainReprint = Printing(
    oracleId = "6e880df6-fc00-43d2-a9c8-f575f40b78c6",
    name = "Stone Rain",
    setCode = "POR",
    collectorNumber = "151",
    artist = "John Matson",
    imageUri = "https://cards.scryfall.io/normal/front/5/7/57f84a13-d7dc-491b-a77c-1b99b6797d7e.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
