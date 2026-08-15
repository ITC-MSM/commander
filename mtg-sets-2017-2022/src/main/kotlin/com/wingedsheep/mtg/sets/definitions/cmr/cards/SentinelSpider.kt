package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sentinel Spider reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M13's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SentinelSpiderReprint = Printing(
    oracleId = "a37c9b13-dc45-4b55-a63d-5314abde7aab",
    name = "Sentinel Spider",
    setCode = "CMR",
    collectorNumber = "253",
    artist = "Vincent Proce",
    imageUri = "https://cards.scryfall.io/normal/front/0/1/01e7af76-e505-49ca-a91e-8167027560ff.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
