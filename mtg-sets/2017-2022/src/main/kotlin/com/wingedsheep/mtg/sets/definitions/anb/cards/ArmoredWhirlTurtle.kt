package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Armored Whirl Turtle reprint in ANB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * GS1's `cards/` package (the card's earliest real printing). This file
 * contributes only the ANB-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ArmoredWhirlTurtleReprint = Printing(
    oracleId = "51a886f2-9b0a-4964-9d59-99dc1a68a97c",
    name = "Armored Whirl Turtle",
    setCode = "ANB",
    collectorNumber = "24",
    artist = "Tingting Yeh",
    imageUri = "https://cards.scryfall.io/normal/front/4/4/44a783f2-04d3-42fc-acc1-5f2974f9aca2.jpg?1783929839",
    releaseDate = "2020-08-13",
    rarity = Rarity.COMMON,
)
