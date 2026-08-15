package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jungle Hollow reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val JungleHollowReprint = Printing(
    oracleId = "6de714e1-446d-4fb9-9e3d-bcd3ec6af9ca",
    name = "Jungle Hollow",
    setCode = "MOM",
    collectorNumber = "270",
    scryfallId = "b6ed20a4-bc8a-44b1-b9b7-c82518c287b8",
    artist = "Thomas Stoop",
    imageUri = "https://cards.scryfall.io/normal/front/b/6/b6ed20a4-bc8a-44b1-b9b7-c82518c287b8.jpg?1682205862",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
