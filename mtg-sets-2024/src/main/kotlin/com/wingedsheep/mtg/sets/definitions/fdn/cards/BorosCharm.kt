package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Boros Charm reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GTC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BorosCharmReprint = Printing(
    oracleId = "2679d0dd-ba30-4a1c-b6a0-b3ac6c790496",
    name = "Boros Charm",
    setCode = "FDN",
    collectorNumber = "721",
    artist = "Zoltan Boros",
    imageUri = "https://cards.scryfall.io/normal/front/e/0/e0d8c9f6-cbbd-4694-b100-01cfb81036cc.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
