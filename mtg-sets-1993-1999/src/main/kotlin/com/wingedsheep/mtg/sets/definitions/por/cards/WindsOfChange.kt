package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Winds of Change reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEG's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WindsOfChangeReprint = Printing(
    oracleId = "f525cf10-e24c-4c46-9a13-6f8579d09d50",
    name = "Winds of Change",
    setCode = "POR",
    collectorNumber = "156",
    artist = "Adam Rex",
    imageUri = "https://cards.scryfall.io/normal/front/7/3/735b8aec-62d4-46db-9a68-a6c69cb6fd98.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.RARE,
)
