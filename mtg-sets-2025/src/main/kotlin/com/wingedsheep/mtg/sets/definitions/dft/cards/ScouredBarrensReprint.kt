package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Scoured Barrens reprint in DFT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DFT-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ScouredBarrensReprint = Printing(
    oracleId = "d37f858e-03c8-4594-9b92-cd03699a1591",
    name = "Scoured Barrens",
    setCode = "DFT",
    collectorNumber = "263",
    scryfallId = "ac52be0c-49f4-4956-820e-bdd7d2744d2f",
    artist = "Eddie Mendoza",
    imageUri = "https://cards.scryfall.io/normal/front/a/c/ac52be0c-49f4-4956-820e-bdd7d2744d2f.jpg?1738356941",
    releaseDate = "2025-02-14",
    rarity = Rarity.COMMON,
)
