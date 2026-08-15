package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tribute to Hunger reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in ISD's `cards/` package
 * (the card's earliest real printing). This file contributes only the FDN presentation row —
 * set, collector number, art — picked up automatically by `CardDiscovery.findPrintingsIn`.
 */
val TributeToHungerReprint = Printing(
    oracleId = "dc5a501c-e16c-4d4b-a510-70a14272473e",
    name = "Tribute to Hunger",
    setCode = "FDN",
    collectorNumber = "614",
    scryfallId = "11f1b897-bb7d-4217-97a7-c89f2453c8fa",
    artist = "Dave Kendall",
    imageUri = "https://cards.scryfall.io/normal/front/1/1/11f1b897-bb7d-4217-97a7-c89f2453c8fa.jpg?1782688731",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
