package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Pillarfield Ox reprint in M14.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * ZEN's `cards/` package (the card's earliest real printing). This file
 * contributes only the M14-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PillarfieldOxReprint = Printing(
    oracleId = "0f7c70c4-9795-4c76-8b27-043942a963c6",
    name = "Pillarfield Ox",
    setCode = "M14",
    collectorNumber = "28",
    artist = "Andrew Robinson",
    imageUri = "https://cards.scryfall.io/normal/front/f/7/f79d3bba-18b0-4c56-a90b-8e28935a6a7a.jpg?1783939941",
    releaseDate = "2013-07-19",
    rarity = Rarity.COMMON,
)
