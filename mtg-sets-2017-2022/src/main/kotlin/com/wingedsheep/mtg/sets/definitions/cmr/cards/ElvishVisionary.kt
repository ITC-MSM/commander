package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Elvish Visionary reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ALA's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ElvishVisionaryReprint = Printing(
    oracleId = "c6a3a882-a127-4590-93d7-679ef4313efe",
    name = "Elvish Visionary",
    setCode = "CMR",
    collectorNumber = "223",
    artist = "D. Alexander Gregory",
    imageUri = "https://cards.scryfall.io/normal/front/a/2/a2f174e6-9532-4fc3-815b-2dc3966c6523.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
