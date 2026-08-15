package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Angel of the Dawn reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AngelOfTheDawnReprint = Printing(
    oracleId = "f63ea0e5-7769-4a22-bbb3-658281141d8c",
    name = "Angel of the Dawn",
    setCode = "CMR",
    collectorNumber = "6",
    artist = "Livia Prima",
    imageUri = "https://cards.scryfall.io/normal/front/d/0/d00ffeb5-195c-45ad-a791-ec67f463d552.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
