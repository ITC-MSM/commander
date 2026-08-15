package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rugged Highlands reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RuggedHighlandsReprint = Printing(
    oracleId = "6c922206-6e68-4dcd-9559-88da1074f2c4",
    name = "Rugged Highlands",
    setCode = "MOM",
    collectorNumber = "271",
    scryfallId = "3aeef1b1-a351-47ce-a686-a0eb0a35a894",
    artist = "Thomas Stoop",
    imageUri = "https://cards.scryfall.io/normal/front/3/a/3aeef1b1-a351-47ce-a686-a0eb0a35a894.jpg?1682205872",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
