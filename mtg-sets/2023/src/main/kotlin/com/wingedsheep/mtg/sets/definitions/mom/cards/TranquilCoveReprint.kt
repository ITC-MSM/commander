package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tranquil Cove reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TranquilCoveReprint = Printing(
    oracleId = "5d641bf6-0f93-4189-8dc1-ec7ea446dade",
    name = "Tranquil Cove",
    setCode = "MOM",
    collectorNumber = "275",
    scryfallId = "3799dcb2-7cd7-4d28-b9af-249e3ebe3d3b",
    artist = "Chris Ostrowski",
    imageUri = "https://cards.scryfall.io/normal/front/3/7/3799dcb2-7cd7-4d28-b9af-249e3ebe3d3b.jpg?1682205913",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
