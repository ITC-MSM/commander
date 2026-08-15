package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bolt Bend reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * WAR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BoltBendReprint = Printing(
    oracleId = "c20a96f7-aa5a-4c15-b8b1-806685c99b27",
    name = "Bolt Bend",
    setCode = "FDN",
    collectorNumber = "619",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/c/f/cf01af2c-68e5-4bb5-81de-f3a1b860fb2e.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
