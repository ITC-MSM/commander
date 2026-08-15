package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Aurelia, the Warleader reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GTC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AureliaTheWarleaderReprint = Printing(
    oracleId = "0f5a3a09-2f07-4774-9e0f-e99d9a444166",
    name = "Aurelia, the Warleader",
    setCode = "FDN",
    collectorNumber = "651",
    artist = "Slawomir Maniak",
    imageUri = "https://cards.scryfall.io/normal/front/b/c/bc6ffc1c-575b-4116-83c9-d13b29886c35.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)
