package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Negate reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val NegateReprint = Printing(
    oracleId = "3407fe41-fdd3-4119-8f70-4bc4590a379f",
    name = "Negate",
    setCode = "MOM",
    collectorNumber = "68",
    scryfallId = "81752db1-374e-4723-b695-a2f4a634dfc6",
    artist = "Viko Menezes",
    imageUri = "https://cards.scryfall.io/normal/front/8/1/81752db1-374e-4723-b695-a2f4a634dfc6.jpg?1682203304",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
