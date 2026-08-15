package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Hulking Cyclops reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HulkingCyclopsReprint = Printing(
    oracleId = "04936918-7ef3-42fa-b0cd-87d18cb69d2c",
    name = "Hulking Cyclops",
    setCode = "POR",
    collectorNumber = "134",
    artist = "Paolo Parente",
    imageUri = "https://cards.scryfall.io/normal/front/f/2/f20ae982-8a70-4dd3-8254-0d447954f580.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.UNCOMMON,
)
