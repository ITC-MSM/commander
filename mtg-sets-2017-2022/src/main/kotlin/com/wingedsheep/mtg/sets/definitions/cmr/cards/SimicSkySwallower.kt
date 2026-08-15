package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Simic Sky Swallower reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SimicSkySwallowerReprint = Printing(
    oracleId = "e5c48c6d-a7bf-45b3-b48c-dc884d49c5f7",
    name = "Simic Sky Swallower",
    setCode = "CMR",
    collectorNumber = "452",
    artist = "rk post",
    imageUri = "https://cards.scryfall.io/normal/front/f/7/f791b7fa-3d12-409d-b017-cb9fd8b71af7.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
)
