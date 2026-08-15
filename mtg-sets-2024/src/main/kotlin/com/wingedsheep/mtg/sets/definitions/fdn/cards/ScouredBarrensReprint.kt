package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Scoured Barrens reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ScouredBarrensReprint = Printing(
    oracleId = "d37f858e-03c8-4594-9b92-cd03699a1591",
    name = "Scoured Barrens",
    setCode = "FDN",
    collectorNumber = "266",
    scryfallId = "2632a4b2-9ca6-4b67-9a99-14f52ad3dc41",
    artist = "Eytan Zana",
    imageUri = "https://cards.scryfall.io/normal/front/2/6/2632a4b2-9ca6-4b67-9a99-14f52ad3dc41.jpg?1730489596",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
