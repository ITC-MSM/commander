package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Pilgrim's Eye reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * WWK's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PilgrimsEyeReprint = Printing(
    oracleId = "66155010-cc2c-449a-85d5-92c95aade514",
    name = "Pilgrim's Eye",
    setCode = "CMR",
    collectorNumber = "332",
    artist = "Dan Murayama Scott",
    imageUri = "https://cards.scryfall.io/normal/front/4/5/45f48d5f-7e01-424d-8e86-1d4a32f0ef34.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
