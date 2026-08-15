package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rune-Scarred Demon reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M12's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RuneScarredDemonReprint = Printing(
    oracleId = "14aefe99-fb60-4f1c-a71f-7ffbe94c8b13",
    name = "Rune-Scarred Demon",
    setCode = "FDN",
    collectorNumber = "184",
    artist = "Michael Komarck",
    imageUri = "https://cards.scryfall.io/normal/front/1/e/1eae3165-554d-4759-8f18-794e2a7d8464.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
