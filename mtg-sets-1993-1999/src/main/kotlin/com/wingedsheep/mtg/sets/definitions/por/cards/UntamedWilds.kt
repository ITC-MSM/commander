package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Untamed Wilds reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEG's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val UntamedWildsReprint = Printing(
    oracleId = "b3b4c21d-f8d7-455f-be46-d5eb909d54df",
    name = "Untamed Wilds",
    setCode = "POR",
    collectorNumber = "191",
    artist = "Romas Kukalis",
    imageUri = "https://cards.scryfall.io/normal/front/1/f/1f4fd77e-ee43-4de7-9ee8-1075ff70b5e7.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.UNCOMMON,
)
