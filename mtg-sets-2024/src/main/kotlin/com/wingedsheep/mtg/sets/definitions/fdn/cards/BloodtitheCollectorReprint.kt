package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodtithe Collector reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in MID's
 * `cards/` package (the card's earliest real printing). This file contributes only the
 * FDN-specific presentation row.
 */
val BloodtitheCollectorReprint = Printing(
    oracleId = "93088b0c-d158-473a-9c06-aa2f344a9868",
    name = "Bloodtithe Collector",
    setCode = "FDN",
    collectorNumber = "751",
    scryfallId = "37931135-100d-4a23-a6e3-baf90fb259ee",
    artist = "Maria Zolotukhina",
    imageUri = "https://cards.scryfall.io/normal/front/3/7/37931135-100d-4a23-a6e3-baf90fb259ee.jpg?1783903950",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
