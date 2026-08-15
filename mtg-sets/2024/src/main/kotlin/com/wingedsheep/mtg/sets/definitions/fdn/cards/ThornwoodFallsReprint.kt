package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Thornwood Falls reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ThornwoodFallsReprint = Printing(
    oracleId = "ec96cde2-f1e6-495c-94e2-3e8ae79e556c",
    name = "Thornwood Falls",
    setCode = "FDN",
    collectorNumber = "269",
    scryfallId = "42799f51-0f8c-444b-974e-dae281a5c697",
    artist = "Eytan Zana",
    imageUri = "https://cards.scryfall.io/normal/front/4/2/42799f51-0f8c-444b-974e-dae281a5c697.jpg?1730489604",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
