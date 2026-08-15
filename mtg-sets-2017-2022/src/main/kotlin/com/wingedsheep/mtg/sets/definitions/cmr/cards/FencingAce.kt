package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fencing Ace reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RTR's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FencingAceReprint = Printing(
    oracleId = "2f810936-2ba6-4c2b-84a0-ff4c1deb026b",
    name = "Fencing Ace",
    setCode = "CMR",
    collectorNumber = "21",
    artist = "David Rapoza",
    imageUri = "https://cards.scryfall.io/normal/front/8/6/86d8754c-816f-4d07-8dcd-9611bf0beb87.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
