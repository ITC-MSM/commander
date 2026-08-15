package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Llanowar Elves reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val LlanowarElvesReprint = Printing(
    oracleId = "68954295-54e3-4303-a6bc-fc4547a4e3a3",
    name = "Llanowar Elves",
    setCode = "FDN",
    collectorNumber = "227",
    scryfallId = "6a0b230b-d391-4998-a3f7-7b158a0ec2cd",
    artist = "Kev Walker",
    imageUri = "https://cards.scryfall.io/normal/front/6/a/6a0b230b-d391-4998-a3f7-7b158a0ec2cd.jpg?1731652605",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
