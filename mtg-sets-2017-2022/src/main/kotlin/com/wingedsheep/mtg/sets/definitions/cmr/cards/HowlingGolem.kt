package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Howling Golem reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DOM's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HowlingGolemReprint = Printing(
    oracleId = "75269a31-3819-4410-8bb7-10de16810a5e",
    name = "Howling Golem",
    setCode = "CMR",
    collectorNumber = "316",
    artist = "Grzegorz Rutkowski",
    imageUri = "https://cards.scryfall.io/normal/front/0/5/05b54aaa-93e2-4f8b-8d06-2a848498d1fd.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
