package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Day of Judgment reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ZEN's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DayOfJudgmentReprint = Printing(
    oracleId = "d057289d-5e28-43d5-8ff3-4a1bc723477d",
    name = "Day of Judgment",
    setCode = "FDN",
    collectorNumber = "140",
    artist = "Vincent Proce",
    imageUri = "https://cards.scryfall.io/normal/front/9/6/96e84bdc-8a9a-4c58-ba8b-9f052fd60069.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
