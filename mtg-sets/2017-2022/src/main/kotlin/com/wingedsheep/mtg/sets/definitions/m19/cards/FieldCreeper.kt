package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Field Creeper reprint in M19.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * EMN's `cards/` package (the card's earliest real printing). This file
 * contributes only the M19-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FieldCreeperReprint = Printing(
    oracleId = "e6584e39-099c-437f-9239-fa718cac85af",
    name = "Field Creeper",
    setCode = "M19",
    collectorNumber = "234",
    artist = "Anthony Palumbo",
    imageUri = "https://cards.scryfall.io/normal/front/e/1/e148c1bf-84a2-48cd-882e-ad0fd74b8f0f.jpg?1783934513",
    releaseDate = "2018-07-13",
    rarity = Rarity.COMMON,
)
