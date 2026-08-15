package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Zur the Enchanter reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * CSP's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ZurTheEnchanterReprint = Printing(
    oracleId = "d7950018-d744-48a8-81aa-0d8384703f48",
    name = "Zur the Enchanter",
    setCode = "CMR",
    collectorNumber = "544",
    artist = "Josu Hernaiz",
    imageUri = "https://cards.scryfall.io/normal/front/1/8/18a9385e-fe0f-41d0-8415-123431aa1fee.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.MYTHIC,
)
