package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Run Away Together reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RunAwayTogetherReprint = Printing(
    oracleId = "290faa28-450e-4797-9a8f-642d8af3f82a",
    name = "Run Away Together",
    setCode = "FDN",
    collectorNumber = "162",
    scryfallId = "e598eb7b-10dc-49e6-ac60-2fefa987173e",
    artist = "Filip Burburan",
    imageUri = "https://cards.scryfall.io/normal/front/e/5/e598eb7b-10dc-49e6-ac60-2fefa987173e.jpg?1730489206",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
