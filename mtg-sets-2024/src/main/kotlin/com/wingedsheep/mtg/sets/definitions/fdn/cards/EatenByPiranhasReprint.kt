package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Eaten by Piranhas reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in LCI's `cards/` package
 * (the card's earliest real printing). This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EatenByPiranhasReprint = Printing(
    oracleId = "c37eae65-1afe-4242-b677-24dd40e6f401",
    name = "Eaten by Piranhas",
    setCode = "FDN",
    collectorNumber = "507",
    artist = "Abz J Harding",
    imageUri = "https://cards.scryfall.io/normal/front/4/7/475e28bb-1333-45e1-b6fd-83121c2f1ab9.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
