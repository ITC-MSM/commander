package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sanctuary Cat reprint in ANB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * DKA's `cards/` package (the card's earliest real printing). This file
 * contributes only the ANB-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SanctuaryCatReprint = Printing(
    oracleId = "c623bc4b-7fc1-4bc1-84e9-71c301dd402f",
    name = "Sanctuary Cat",
    setCode = "ANB",
    collectorNumber = "17",
    artist = "David Palumbo",
    imageUri = "https://cards.scryfall.io/normal/front/6/1/61170c40-1e85-4fc2-804a-7fdc7062ac55.jpg?1783929839",
    releaseDate = "2020-08-13",
    rarity = Rarity.COMMON,
)
