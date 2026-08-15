package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Grow from the Ashes reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GrowFromTheAshesReprint = Printing(
    oracleId = "645ab3c7-ee1d-4dd0-811f-2dc7f7c7e792",
    name = "Grow from the Ashes",
    setCode = "FDN",
    collectorNumber = "225",
    scryfallId = "42525f8a-aee7-4811-8f05-471b559c2c4a",
    artist = "Richard Wright",
    imageUri = "https://cards.scryfall.io/normal/front/4/2/42525f8a-aee7-4811-8f05-471b559c2c4a.jpg?1730489438",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
