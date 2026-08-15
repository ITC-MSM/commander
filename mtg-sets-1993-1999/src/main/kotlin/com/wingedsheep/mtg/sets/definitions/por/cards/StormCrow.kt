package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Storm Crow reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ALL's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StormCrowReprint = Printing(
    oracleId = "000d5588-5a4c-434e-988d-396632ade42c",
    name = "Storm Crow",
    setCode = "POR",
    collectorNumber = "69",
    artist = "Una Fricker",
    imageUri = "https://cards.scryfall.io/normal/front/d/f/dfe87b59-b456-4532-a695-0dea3110d878.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
