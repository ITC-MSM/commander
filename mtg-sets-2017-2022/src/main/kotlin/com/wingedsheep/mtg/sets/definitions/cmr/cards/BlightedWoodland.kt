package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Blighted Woodland reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * BFZ's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BlightedWoodlandReprint = Printing(
    oracleId = "02679a2e-303d-412f-87d8-0a37a8ca259c",
    name = "Blighted Woodland",
    setCode = "CMR",
    collectorNumber = "476",
    artist = "Jason Felix",
    imageUri = "https://cards.scryfall.io/normal/front/0/a/0a949030-a46c-4bac-bedc-c07d5c8464af.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
