package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Soul-Guide Lantern reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in THB's `cards/` package
 * (the card's earliest real printing). This file contributes only the FDN presentation row —
 * set, collector number, art — picked up automatically by `CardDiscovery.findPrintingsIn`.
 */
val SoulGuideLanternReprint = Printing(
    oracleId = "1b5e6560-ff2e-4475-96cb-63f64c8a86db",
    name = "Soul-Guide Lantern",
    setCode = "FDN",
    collectorNumber = "680",
    scryfallId = "6d3ff537-86f0-405e-b96b-1250720af031",
    artist = "Iris Compiet",
    imageUri = "https://cards.scryfall.io/normal/front/6/d/6d3ff537-86f0-405e-b96b-1250720af031.jpg?1782688674",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
