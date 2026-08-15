package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Phyrexian Rager reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * APC's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PhyrexianRagerReprint = Printing(
    oracleId = "e409c9be-0c9a-43c3-adb4-8c47afc1d551",
    name = "Phyrexian Rager",
    setCode = "CMR",
    collectorNumber = "142",
    artist = "Stephan Martiniere",
    imageUri = "https://cards.scryfall.io/normal/front/b/b/bb0d354e-3a63-4dfe-ae6d-5e82cbf419ac.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
