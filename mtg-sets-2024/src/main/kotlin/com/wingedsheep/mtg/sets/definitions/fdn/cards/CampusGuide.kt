package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Campus Guide reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * STX's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CampusGuideReprint = Printing(
    oracleId = "5b8e0913-47e9-4d56-971d-abd91b0f7587",
    name = "Campus Guide",
    setCode = "FDN",
    collectorNumber = "251",
    artist = "Slawomir Maniak",
    imageUri = "https://cards.scryfall.io/normal/front/4/3/43c59814-3167-4b05-bb85-6c736f3956a4.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
