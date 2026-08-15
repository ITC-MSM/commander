package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Negate reprint in DMU.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DMU-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val NegateReprint = Printing(
    oracleId = "3407fe41-fdd3-4119-8f70-4bc4590a379f",
    name = "Negate",
    setCode = "DMU",
    collectorNumber = "58",
    scryfallId = "4016c6f7-7cb4-46c2-af73-3bd6d682ea5e",
    artist = "Isis",
    imageUri = "https://cards.scryfall.io/normal/front/4/0/4016c6f7-7cb4-46c2-af73-3bd6d682ea5e.jpg?1673306788",
    releaseDate = "2022-09-09",
    rarity = Rarity.COMMON,
)
