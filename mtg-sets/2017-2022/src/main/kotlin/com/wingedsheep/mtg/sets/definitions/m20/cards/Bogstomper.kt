package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bogstomper reprint in M20.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file
 * contributes only the M20-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BogstomperReprint = Printing(
    oracleId = "7c2f1915-8d91-47c6-bab4-be325348673a",
    name = "Bogstomper",
    setCode = "M20",
    collectorNumber = "320",
    artist = "Jason Felix",
    imageUri = "https://cards.scryfall.io/normal/front/a/d/ad005eef-d4e4-4f46-81a5-9bbce87014ce.jpg?1783932908",
    releaseDate = "2019-07-12",
    rarity = Rarity.COMMON,
)
