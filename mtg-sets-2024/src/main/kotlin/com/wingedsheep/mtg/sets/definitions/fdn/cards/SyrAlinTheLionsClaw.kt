package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Syr Alin, the Lion's Claw reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ELD's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SyrAlinTheLionsClawReprint = Printing(
    oracleId = "5d9a4905-4a26-410e-936c-56de849e9ca3",
    name = "Syr Alin, the Lion's Claw",
    setCode = "FDN",
    collectorNumber = "582",
    artist = "Paul Scott Canavan",
    imageUri = "https://cards.scryfall.io/normal/front/5/d/5d42be5f-a6a7-4699-abf7-9632de6daede.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
