package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Knight of Malice reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val KnightOfMaliceReprint = Printing(
    oracleId = "b89d912a-65a0-4bc9-9e1d-ff8317458a72",
    name = "Knight of Malice",
    setCode = "FDN",
    collectorNumber = "608",
    scryfallId = "bb678e10-e5a4-4143-8e49-7b523118674e",
    artist = "Sidharth Chaturvedi",
    imageUri = "https://cards.scryfall.io/normal/front/b/b/bb678e10-e5a4-4143-8e49-7b523118674e.jpg?1730490907",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
