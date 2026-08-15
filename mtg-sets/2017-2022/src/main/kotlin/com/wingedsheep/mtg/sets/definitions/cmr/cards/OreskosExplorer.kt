package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Oreskos Explorer reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * C15's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OreskosExplorerReprint = Printing(
    oracleId = "a3055ae8-990d-495a-9cb5-971407990cfa",
    name = "Oreskos Explorer",
    setCode = "CMR",
    collectorNumber = "381",
    artist = "Winona Nelson",
    imageUri = "https://cards.scryfall.io/normal/front/1/8/18cf5b23-dad0-4d4f-bd73-0133dbc9f8e1.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
