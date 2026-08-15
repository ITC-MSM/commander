package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Impact Tremors reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DTK's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ImpactTremorsReprint = Printing(
    oracleId = "9242cd3e-1a71-4700-8182-9c1005616033",
    name = "Impact Tremors",
    setCode = "FDN",
    collectorNumber = "717",
    artist = "Lake Hurwitz",
    imageUri = "https://cards.scryfall.io/normal/front/d/0/d0b7cecf-b51b-4d30-b7e9-cd7976271e07.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
