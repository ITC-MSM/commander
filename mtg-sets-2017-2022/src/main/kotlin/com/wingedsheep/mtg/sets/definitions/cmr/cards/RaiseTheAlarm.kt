package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Raise the Alarm reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MRD's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RaiseTheAlarmReprint = Printing(
    oracleId = "5b2364d7-a811-4595-a1b4-224c70555ffa",
    name = "Raise the Alarm",
    setCode = "CMR",
    collectorNumber = "41",
    artist = "Zoltan Boros",
    imageUri = "https://cards.scryfall.io/normal/front/6/c/6c7c8527-55f6-494d-b4f7-c427a5735053.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
