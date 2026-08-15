package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodfell Caves reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BloodfellCavesReprint = Printing(
    oracleId = "64e29bfc-9313-4e8c-808c-bc27f6b018a6",
    name = "Bloodfell Caves",
    setCode = "FDN",
    collectorNumber = "259",
    scryfallId = "8b90dc92-cb66-41d9-89f9-2b6e3cfc8082",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/8/b/8b90dc92-cb66-41d9-89f9-2b6e3cfc8082.jpg?1730489569",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
