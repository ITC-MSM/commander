package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Treetop Warden reprint in ANB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * OANA's `cards/` package (the card's earliest real printing). This file
 * contributes only the ANB-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TreetopWardenReprint = Printing(
    oracleId = "8bb63574-6e8e-499b-8e61-843f06b19313",
    name = "Treetop Warden",
    setCode = "ANB",
    collectorNumber = "107",
    artist = "Colin Boyer",
    imageUri = "https://cards.scryfall.io/normal/front/2/2/22c42e1a-9a4d-4630-b6c2-749d76c4cafb.jpg?1783929784",
    releaseDate = "2020-08-13",
    rarity = Rarity.COMMON,
)
