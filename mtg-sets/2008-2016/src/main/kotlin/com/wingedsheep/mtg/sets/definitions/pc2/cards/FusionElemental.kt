package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fusion Elemental reprint in PC2.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * CON's `cards/` package (the card's earliest real printing). This file
 * contributes only the PC2-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FusionElementalReprint = Printing(
    oracleId = "a152674f-be29-40ab-8dd9-376fd4eb3bb8",
    name = "Fusion Elemental",
    setCode = "PC2",
    collectorNumber = "93",
    artist = "Michael Komarck",
    imageUri = "https://cards.scryfall.io/normal/front/d/e/de5a1e9e-ac2b-4db7-8d6e-554c4c65f1cb.jpg?1783940599",
    releaseDate = "2012-06-01",
    rarity = Rarity.UNCOMMON,
)
