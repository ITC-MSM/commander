package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Winds of Rath reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * TMP's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WindsOfRathReprint = Printing(
    oracleId = "a6fd90dc-0ec3-4dce-a77a-4d04f5e254bf",
    name = "Winds of Rath",
    setCode = "CMR",
    collectorNumber = "392",
    artist = "Drew Tucker",
    imageUri = "https://cards.scryfall.io/normal/front/6/7/67b91ab9-a03b-4036-9ad4-5c9f7743cd52.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
)
