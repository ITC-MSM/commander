package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Catacomb Slug reprint in ORI.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * RTR's `cards/` package (the card's earliest real printing). This file
 * contributes only the ORI-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CatacombSlugReprint = Printing(
    oracleId = "1b0b5264-2810-4ac5-8b2e-8abbf4016287",
    name = "Catacomb Slug",
    setCode = "ORI",
    collectorNumber = "86",
    artist = "Nils Hamm",
    imageUri = "https://cards.scryfall.io/normal/front/d/3/d30d6df7-6199-4b06-9d45-785ee1e2ed3b.jpg?1783938344",
    releaseDate = "2015-07-17",
    rarity = Rarity.COMMON,
)
