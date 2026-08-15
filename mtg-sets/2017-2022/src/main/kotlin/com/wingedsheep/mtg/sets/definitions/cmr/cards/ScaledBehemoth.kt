package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Scaled Behemoth reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * AKH's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ScaledBehemothReprint = Printing(
    oracleId = "abb39d0e-bf11-4982-b06a-ca7be16bce2c",
    name = "Scaled Behemoth",
    setCode = "CMR",
    collectorNumber = "251",
    artist = "Marco Nelor",
    imageUri = "https://cards.scryfall.io/normal/front/0/1/017ef6eb-7a2b-4730-bf21-a2289d4c07ad.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
