package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunhome, Fortress of the Legion reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RAV's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SunhomeFortressOfTheLegionReprint = Printing(
    oracleId = "a9f8344c-1705-4254-81d6-aa05e0c69c29",
    name = "Sunhome, Fortress of the Legion",
    setCode = "CMR",
    collectorNumber = "496",
    artist = "Martina Pilcerova",
    imageUri = "https://cards.scryfall.io/normal/front/5/f/5f7071a2-2ff2-405b-9052-f902dee820a7.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
