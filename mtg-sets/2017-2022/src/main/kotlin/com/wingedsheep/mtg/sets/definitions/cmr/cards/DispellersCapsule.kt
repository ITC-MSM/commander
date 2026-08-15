package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dispeller's Capsule reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ALA's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DispellersCapsuleReprint = Printing(
    oracleId = "91fc862d-fdde-45ea-a05d-30d38e7a735c",
    name = "Dispeller's Capsule",
    setCode = "CMR",
    collectorNumber = "18",
    artist = "Franz Vohwinkel",
    imageUri = "https://cards.scryfall.io/normal/front/d/9/d9b717e3-4dc6-4a7d-928a-2111c8199177.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
