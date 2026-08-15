package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rumbling Baloth reprint in JMP.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M14's `cards/` package (the card's earliest real printing). This file
 * contributes only the JMP-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RumblingBalothReprint = Printing(
    oracleId = "3191c6ca-4d25-4ba3-bfe1-4aeab1295573",
    name = "Rumbling Baloth",
    setCode = "JMP",
    collectorNumber = "426",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/9/3/93a56610-482b-4ddf-88e1-e4a2edf4fa0f.jpg?1783930355",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
