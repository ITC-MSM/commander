package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Elvish Ranger reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ALL's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ElvishRangerReprint = Printing(
    oracleId = "092f2c34-1f75-4998-b219-2cf1ca73656d",
    name = "Elvish Ranger",
    setCode = "POR",
    collectorNumber = "165",
    artist = "DiTerlizzi",
    imageUri = "https://cards.scryfall.io/normal/front/2/6/26caff65-3a96-46f2-8f0b-e5091b632a2e.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
