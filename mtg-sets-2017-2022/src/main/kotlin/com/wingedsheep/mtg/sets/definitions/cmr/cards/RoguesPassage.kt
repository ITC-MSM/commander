package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rogue's Passage reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RTR's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RoguesPassageReprint = Printing(
    oracleId = "f29dc596-2121-4421-8463-15f6c2e8b9b3",
    name = "Rogue's Passage",
    setCode = "CMR",
    collectorNumber = "489",
    artist = "Christine Choi",
    imageUri = "https://cards.scryfall.io/normal/front/4/5/45bbfd91-40d7-4cb4-ac82-27c50d872cf5.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
