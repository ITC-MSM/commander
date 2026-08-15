package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bitter Revelation reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * KTK's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BitterRevelationReprint = Printing(
    oracleId = "f3e025ce-6114-42d0-842f-73366c853657",
    name = "Bitter Revelation",
    setCode = "CMR",
    collectorNumber = "109",
    artist = "Viktor Titov",
    imageUri = "https://cards.scryfall.io/normal/front/2/5/25657da2-e7bc-4e9b-8ec2-5a5779738436.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
