package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Maelstrom Wanderer reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * PC2's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MaelstromWandererReprint = Printing(
    oracleId = "ad9b7fbc-61c8-43ee-a65c-99206fd1e4df",
    name = "Maelstrom Wanderer",
    setCode = "CMR",
    collectorNumber = "526",
    artist = "Victor Adame Minguez",
    imageUri = "https://cards.scryfall.io/normal/front/d/5/d5a75062-b5e3-4942-af64-12f43101c294.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.MYTHIC,
)
