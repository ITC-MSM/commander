package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Macabre Waltz reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MacabreWaltzReprint = Printing(
    oracleId = "cf75dbd8-b65a-48d9-b96b-4afb43f336d1",
    name = "Macabre Waltz",
    setCode = "FDN",
    collectorNumber = "177",
    artist = "Willian Murai",
    imageUri = "https://cards.scryfall.io/normal/front/4/d/4d1f3c84-89ba-4426-a80b-d524f172c912.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
