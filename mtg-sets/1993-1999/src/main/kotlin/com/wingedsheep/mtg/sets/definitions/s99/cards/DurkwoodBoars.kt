package com.wingedsheep.mtg.sets.definitions.s99.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Durkwood Boars reprint in S99.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * LEG's `cards/` package (the card's earliest real printing). This file
 * contributes only the S99-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DurkwoodBoarsReprint = Printing(
    oracleId = "8bb2ac6c-20aa-46dd-883f-b629855cabb0",
    name = "Durkwood Boars",
    setCode = "S99",
    collectorNumber = "127",
    artist = "Mike Kimble",
    imageUri = "https://cards.scryfall.io/normal/front/3/e/3e2275b3-08ea-48d4-8781-7e64f2b94d72.jpg?1783946023",
    releaseDate = "1999-07-01",
    rarity = Rarity.COMMON,
)
