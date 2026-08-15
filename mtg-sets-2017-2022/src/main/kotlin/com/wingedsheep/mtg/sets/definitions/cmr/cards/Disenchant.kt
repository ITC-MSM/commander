package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Disenchant reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DisenchantReprint = Printing(
    oracleId = "a7e97fa9-4b72-4548-b854-5be5f18a6f1a",
    name = "Disenchant",
    setCode = "CMR",
    collectorNumber = "372",
    artist = "Victor Adame Minguez",
    imageUri = "https://cards.scryfall.io/normal/front/2/b/2ba38105-bada-449a-ab2f-3d6db2764a06.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
