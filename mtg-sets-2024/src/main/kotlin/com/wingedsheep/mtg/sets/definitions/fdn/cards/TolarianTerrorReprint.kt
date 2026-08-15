package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tolarian Terror reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TolarianTerrorReprint = Printing(
    oracleId = "b9d0f2e1-62c2-44fd-ad38-471daf17bb0a",
    name = "Tolarian Terror",
    setCode = "FDN",
    collectorNumber = "167",
    scryfallId = "2569d4f3-55ed-4f99-9592-34c7df0aab72",
    artist = "Vincent Christiaens",
    imageUri = "https://cards.scryfall.io/normal/front/2/5/2569d4f3-55ed-4f99-9592-34c7df0aab72.jpg?1730489226",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
