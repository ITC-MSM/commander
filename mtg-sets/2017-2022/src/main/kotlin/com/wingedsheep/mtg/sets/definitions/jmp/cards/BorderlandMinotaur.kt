package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Borderland Minotaur reprint in JMP.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * THS's `cards/` package (the card's earliest real printing). This file
 * contributes only the JMP-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BorderlandMinotaurReprint = Printing(
    oracleId = "03a9ac3f-f6e4-4ade-9770-cf723434b7e8",
    name = "Borderland Minotaur",
    setCode = "JMP",
    collectorNumber = "301",
    artist = "Greg Staples",
    imageUri = "https://cards.scryfall.io/normal/front/8/b/8b8c80ea-7b29-4335-ba7b-3e51a5a104a9.jpg?1783930400",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
