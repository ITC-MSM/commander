package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Forgotten Cave reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ForgottenCaveReprint = Printing(
    oracleId = "394c6de5-7957-4a0b-a6b9-ee0c707cd022",
    name = "Forgotten Cave",
    setCode = "BLC",
    collectorNumber = "305",
    scryfallId = "e8ae2de4-0460-40b4-aa41-70f95919923e",
    artist = "Tony Szczudlo",
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e8ae2de4-0460-40b4-aa41-70f95919923e.jpg?1721429731",
    releaseDate = "2024-08-02",
    rarity = Rarity.COMMON,
)
