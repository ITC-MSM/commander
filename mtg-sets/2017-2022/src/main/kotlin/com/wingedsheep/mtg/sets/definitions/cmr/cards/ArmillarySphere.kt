package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Armillary Sphere reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * CON's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ArmillarySphereReprint = Printing(
    oracleId = "3963140c-da67-43e6-9514-fe9dc0a43c4d",
    name = "Armillary Sphere",
    setCode = "CMR",
    collectorNumber = "298",
    artist = "Franz Vohwinkel",
    imageUri = "https://cards.scryfall.io/normal/front/a/f/af3ae93e-6094-483d-b2c0-ae117ad01293.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
