package com.wingedsheep.mtg.sets.definitions.oana.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sanctuary Cat reprint in OANA.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * DKA's `cards/` package (the card's earliest real printing). This file
 * contributes only the OANA-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SanctuaryCatReprint = Printing(
    oracleId = "c623bc4b-7fc1-4bc1-84e9-71c301dd402f",
    name = "Sanctuary Cat",
    setCode = "OANA",
    collectorNumber = "8",
    artist = "David Palumbo",
    imageUri = "https://cards.scryfall.io/normal/front/a/f/af79c8fb-9189-48c2-a7b8-a1097dbaf138.jpg?1783934411",
    releaseDate = "2018-07-14",
    rarity = Rarity.COMMON,
)
