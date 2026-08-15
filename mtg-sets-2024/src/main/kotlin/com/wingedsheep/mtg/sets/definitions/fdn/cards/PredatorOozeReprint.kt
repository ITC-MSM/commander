package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Predator Ooze reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package (Dark Ascension). This file contributes only the
 * FDN-specific presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PredatorOozeReprint = Printing(
    oracleId = "a51ec70d-1356-4403-a858-b99445bac54a",
    name = "Predator Ooze",
    setCode = "FDN",
    collectorNumber = "642",
    scryfallId = "333b3cca-ebbf-4ceb-a6a5-3d49cb2e143a",
    artist = "Ryan Yee",
    imageUri = "https://cards.scryfall.io/normal/front/3/3/333b3cca-ebbf-4ceb-a6a5-3d49cb2e143a.jpg?1730491033",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
