package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Thought Vessel reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * c15 (Commander 2015). This file contributes only the BLC-specific presentation row —
 * set, collector number, art — picked up automatically by `CardDiscovery.findPrintingsIn`
 * and surfaced via the set's `printings`.
 */
val ThoughtVesselReprint = Printing(
    oracleId = "9965d9c5-2ebf-4a6c-930e-55c5890979be",
    name = "Thought Vessel",
    setCode = "BLC",
    collectorNumber = "289",
    scryfallId = "b7a24bfc-bae0-4b21-9054-68723a1adeae",
    artist = "rk post",
    imageUri = "https://cards.scryfall.io/normal/front/b/7/b7a24bfc-bae0-4b21-9054-68723a1adeae.jpg?1721950066",
    releaseDate = "2024-08-02",
    rarity = Rarity.COMMON,
)
