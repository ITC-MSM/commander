package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ancient Crab reprint in AKH.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * OGW's `cards/` package (the card's earliest real printing). This file
 * contributes only the AKH-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AncientCrabReprint = Printing(
    oracleId = "6bab6d0a-cd06-45e3-b780-d5354ee2a032",
    name = "Ancient Crab",
    setCode = "AKH",
    collectorNumber = "40",
    artist = "James Paick",
    imageUri = "https://cards.scryfall.io/normal/front/7/c/7c2ca68b-15fb-4691-b549-268df92ca413.jpg?1783936527",
    releaseDate = "2017-04-28",
    rarity = Rarity.COMMON,
)
