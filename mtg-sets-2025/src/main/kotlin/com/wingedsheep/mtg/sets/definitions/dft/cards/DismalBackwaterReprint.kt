package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dismal Backwater reprint in DFT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DFT-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DismalBackwaterReprint = Printing(
    oracleId = "865a2194-fca0-446e-aae3-ca475cd66e00",
    name = "Dismal Backwater",
    setCode = "DFT",
    collectorNumber = "254",
    scryfallId = "f20aef3f-79f6-4357-8631-1d141f437def",
    artist = "Wayne Wu",
    imageUri = "https://cards.scryfall.io/normal/front/f/2/f20aef3f-79f6-4357-8631-1d141f437def.jpg?1753618484",
    releaseDate = "2025-02-14",
    rarity = Rarity.COMMON,
)
