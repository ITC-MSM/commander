package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Silverback Shaman reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M20's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SilverbackShamanReprint = Printing(
    oracleId = "fc011947-b496-400e-99b8-b368068ba79b",
    name = "Silverback Shaman",
    setCode = "CMR",
    collectorNumber = "255",
    artist = "Mathias Kollros",
    imageUri = "https://cards.scryfall.io/normal/front/8/0/8048bab7-8fd1-446c-80e9-cc2ffb154295.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
