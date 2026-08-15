package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cultivate reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M11's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CultivateReprint = Printing(
    oracleId = "8b755881-a72d-4e21-a369-d2924eb4585a",
    name = "Cultivate",
    setCode = "CMR",
    collectorNumber = "424",
    artist = "Anthony Palumbo",
    imageUri = "https://cards.scryfall.io/normal/front/5/a/5a108064-8143-4b20-a5a2-eeb3b80af82f.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
