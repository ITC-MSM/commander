package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Thermo-Alchemist reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * EMN's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ThermoAlchemistReprint = Printing(
    oracleId = "228fbae1-423e-461d-b8c3-55786938a3cb",
    name = "Thermo-Alchemist",
    setCode = "INR",
    collectorNumber = "174",
    artist = "Raymond Swanland",
    imageUri = "https://cards.scryfall.io/normal/front/7/8/78b194b3-108a-4152-bd37-52c302e25ad6.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.UNCOMMON,
)
