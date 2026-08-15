package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Moment of Craving reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MomentOfCravingReprint = Printing(
    oracleId = "1489551f-5714-471b-a0e6-f40bc3085afc",
    name = "Moment of Craving",
    setCode = "FDN",
    collectorNumber = "524",
    artist = "Steven Belledin",
    imageUri = "https://cards.scryfall.io/normal/front/3/6/3627f9fb-b828-49cd-887d-e2ab3ef43dfb.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
