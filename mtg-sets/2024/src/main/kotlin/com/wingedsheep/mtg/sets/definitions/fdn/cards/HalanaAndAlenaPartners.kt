package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Halana and Alena, Partners reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VOW's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HalanaAndAlenaPartnersReprint = Printing(
    oracleId = "b347be2d-9aa1-42a9-b652-8fb032832f09",
    name = "Halana and Alena, Partners",
    setCode = "FDN",
    collectorNumber = "659",
    artist = "Jason Rainville",
    imageUri = "https://cards.scryfall.io/normal/front/b/6/b649f459-c9dc-495d-8703-b685996bb80c.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
