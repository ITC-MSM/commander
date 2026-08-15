package com.wingedsheep.mtg.sets.definitions.s99.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dakmor Scorpion reprint in S99.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * P02's `cards/` package (the card's earliest real printing). This file
 * contributes only the S99-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DakmorScorpionReprint = Printing(
    oracleId = "008290c2-499d-425b-9a6c-c4aeb47c33ff",
    name = "Dakmor Scorpion",
    setCode = "S99",
    collectorNumber = "73",
    artist = "Randy Gallegos",
    imageUri = "https://cards.scryfall.io/normal/front/6/e/6ed84268-92f7-4790-99b2-f2982b6e0893.jpg?1783946035",
    releaseDate = "1999-07-01",
    rarity = Rarity.COMMON,
)
