package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tranquil Cove reprint in DFT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DFT-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TranquilCoveReprint = Printing(
    oracleId = "5d641bf6-0f93-4189-8dc1-ec7ea446dade",
    name = "Tranquil Cove",
    setCode = "DFT",
    collectorNumber = "267",
    scryfallId = "70688800-7675-4e75-bb3c-9e05b95a685c",
    artist = "Wayne Wu",
    imageUri = "https://cards.scryfall.io/normal/front/7/0/70688800-7675-4e75-bb3c-9e05b95a685c.jpg?1738356962",
    releaseDate = "2025-02-14",
    rarity = Rarity.COMMON,
)
