package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Open the Armory reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * SOI's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OpenTheArmoryReprint = Printing(
    oracleId = "2ecf7771-8061-4710-ad2e-80b092ae0b4b",
    name = "Open the Armory",
    setCode = "CMR",
    collectorNumber = "34",
    artist = "Steve Prescott",
    imageUri = "https://cards.scryfall.io/normal/front/6/d/6db45698-9da9-4cea-b9bf-0f84ab276b51.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
