package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wary Thespian reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MOM's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WaryThespianReprint = Printing(
    oracleId = "448cc829-1596-4aec-b180-c0c1e0566f6b",
    name = "Wary Thespian",
    setCode = "FDN",
    collectorNumber = "235",
    artist = "Billy Christian",
    imageUri = "https://cards.scryfall.io/normal/front/a/3/a3d62d04-0974-4cb5-9a35-5e996c6456e2.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
