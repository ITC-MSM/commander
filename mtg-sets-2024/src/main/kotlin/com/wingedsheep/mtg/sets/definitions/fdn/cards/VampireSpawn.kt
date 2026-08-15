package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vampire Spawn reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * AFR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VampireSpawnReprint = Printing(
    oracleId = "33cee4b4-3d80-46f1-9223-49ed785891a1",
    name = "Vampire Spawn",
    setCode = "FDN",
    collectorNumber = "532",
    artist = "Alex Brock",
    imageUri = "https://cards.scryfall.io/normal/front/b/6/b6fc8d2e-ad63-498d-83b6-5f72b5ae0e67.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
