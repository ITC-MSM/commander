package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Command Tower reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * CMD's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CommandTowerReprint = Printing(
    oracleId = "0895c9b7-ae7d-4bb3-af17-3b75deb50a25",
    name = "Command Tower",
    setCode = "CMR",
    collectorNumber = "350",
    artist = "Ryan Yee",
    imageUri = "https://cards.scryfall.io/normal/front/8/6/86424491-a372-40c4-bfb5-faa2b2d41d4c.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
