package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Kor Cartographer reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ZEN's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val KorCartographerReprint = Printing(
    oracleId = "a5f537f6-3254-45a3-a4e7-d4d4fd7a972d",
    name = "Kor Cartographer",
    setCode = "CMR",
    collectorNumber = "30",
    artist = "Ryan Pancoast",
    imageUri = "https://cards.scryfall.io/normal/front/5/8/583ef638-1ea1-4301-bb86-78cb2b5f3aab.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
