package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Imperious Perfect reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LRW's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ImperiousPerfectReprint = Printing(
    oracleId = "3fa71348-fa4d-4f39-a451-cf1570591991",
    name = "Imperious Perfect",
    setCode = "CMR",
    collectorNumber = "235",
    artist = "Scott M. Fischer",
    imageUri = "https://cards.scryfall.io/normal/front/4/e/4ef8940a-d1d9-460e-9949-89bb30f9e6d6.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
