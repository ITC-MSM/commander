package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Harmless Offering reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (spell script) lives in EMN's
 * `cards/` package (the card's earliest real printing). This file contributes only the
 * FDN-specific presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HarmlessOfferingReprint = Printing(
    oracleId = "f5d32e5f-066b-42cf-8dd1-072a96275313",
    name = "Harmless Offering",
    setCode = "FDN",
    collectorNumber = "625",
    scryfallId = "47082081-bf52-4914-bac2-ace398beff56",
    artist = "Howard Lyon",
    imageUri = "https://cards.scryfall.io/normal/front/4/7/47082081-bf52-4914-bac2-ace398beff56.jpg?1783908922",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
