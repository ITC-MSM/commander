package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wild Celebrants reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * THS's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WildCelebrantsReprint = Printing(
    oracleId = "695661ac-6f50-4a0e-9831-a0e285bab4bf",
    name = "Wild Celebrants",
    setCode = "CMR",
    collectorNumber = "212",
    artist = "Igor Kieryluk",
    imageUri = "https://cards.scryfall.io/normal/front/6/b/6b9e72f4-0087-4a79-b9af-296c8b930a25.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
