package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Yavimaya Elder reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * UDS's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val YavimayaElderReprint = Printing(
    oracleId = "7fd4c452-07f2-492c-9c78-d1c6362d9eec",
    name = "Yavimaya Elder",
    setCode = "CMR",
    collectorNumber = "441",
    artist = "Matt Cavotta",
    imageUri = "https://cards.scryfall.io/normal/front/c/6/c66cfb9b-77bb-4e5a-be31-3a9984af1217.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
