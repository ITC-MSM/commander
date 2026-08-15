package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Simic Signet reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SimicSignetReprint = Printing(
    oracleId = "44503105-3e13-408d-a44f-37d503c61d72",
    name = "Simic Signet",
    setCode = "CMR",
    collectorNumber = "471",
    artist = "Mike Sass",
    imageUri = "https://cards.scryfall.io/normal/front/6/a/6a8ce2ab-d3e6-486c-a577-2d4f2c750cde.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
