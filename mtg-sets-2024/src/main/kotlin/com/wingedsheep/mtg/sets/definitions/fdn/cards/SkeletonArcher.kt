package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Skeleton Archer reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SkeletonArcherReprint = Printing(
    oracleId = "3792763c-7aa0-4055-b8a1-3257e0443ef1",
    name = "Skeleton Archer",
    setCode = "FDN",
    collectorNumber = "526",
    artist = "Randy Vargas",
    imageUri = "https://cards.scryfall.io/normal/front/7/a/7a959048-0d8a-41bb-8a33-52264e525085.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
