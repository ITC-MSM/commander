package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vampiric Rites reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * BFZ's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VampiricRitesReprint = Printing(
    oracleId = "660de988-b6fb-4f36-8006-42af3e7f908d",
    name = "Vampiric Rites",
    setCode = "FDN",
    collectorNumber = "615",
    artist = "Anastasia Ovchinnikova",
    imageUri = "https://cards.scryfall.io/normal/front/9/a/9ae0b1e0-a481-4a98-b67b-0cf8bac53fdd.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
