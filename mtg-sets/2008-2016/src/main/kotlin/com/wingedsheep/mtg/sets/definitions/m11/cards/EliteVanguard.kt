package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Elite Vanguard reprint in M11.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M11-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EliteVanguardReprint = Printing(
    oracleId = "b3b9a87d-cb95-435c-90b6-037406cab32e",
    name = "Elite Vanguard",
    setCode = "M11",
    collectorNumber = "13",
    artist = "Mark Tedin",
    imageUri = "https://cards.scryfall.io/normal/front/8/9/8969be61-afed-4483-902f-739acf57c43c.jpg?1783941835",
    releaseDate = "2010-07-16",
    rarity = Rarity.UNCOMMON,
)
