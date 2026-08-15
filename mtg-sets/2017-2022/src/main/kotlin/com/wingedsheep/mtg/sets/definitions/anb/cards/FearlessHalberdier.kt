package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fearless Halberdier reprint in ANB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * GRN's `cards/` package (the card's earliest real printing). This file
 * contributes only the ANB-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FearlessHalberdierReprint = Printing(
    oracleId = "2ef2f417-2061-4796-974a-92f800ef05e4",
    name = "Fearless Halberdier",
    setCode = "ANB",
    collectorNumber = "69",
    artist = "Suzanne Helmigh",
    imageUri = "https://cards.scryfall.io/normal/front/e/f/ef9351dc-9af0-48d1-9012-7c478fdc34e1.jpg?1783929807",
    releaseDate = "2020-08-13",
    rarity = Rarity.COMMON,
)
