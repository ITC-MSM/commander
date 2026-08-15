package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Elite Vanguard reprint in M12.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M12-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EliteVanguardReprint = Printing(
    oracleId = "b3b9a87d-cb95-435c-90b6-037406cab32e",
    name = "Elite Vanguard",
    setCode = "M12",
    collectorNumber = "15",
    artist = "Mark Tedin",
    imageUri = "https://cards.scryfall.io/normal/front/f/0/f03487e9-f584-4bbd-8335-4dd001a88b52.jpg?1783941103",
    releaseDate = "2011-07-15",
    rarity = Rarity.UNCOMMON,
)
