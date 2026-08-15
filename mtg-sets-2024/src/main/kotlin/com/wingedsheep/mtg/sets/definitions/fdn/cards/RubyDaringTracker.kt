package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ruby, Daring Tracker reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * WOE's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RubyDaringTrackerReprint = Printing(
    oracleId = "5d1b0eee-3a7a-4f22-a40d-7658a368962a",
    name = "Ruby, Daring Tracker",
    setCode = "FDN",
    collectorNumber = "245",
    artist = "Ekaterina Burmak",
    imageUri = "https://cards.scryfall.io/normal/front/f/e/fe3e7dd2-b66d-4218-9fde-f84bec26b7bf.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
