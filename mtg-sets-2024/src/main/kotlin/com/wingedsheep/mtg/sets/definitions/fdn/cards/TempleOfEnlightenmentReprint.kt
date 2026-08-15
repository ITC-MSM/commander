package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Temple of Enlightenment reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TempleOfEnlightenmentReprint = Printing(
    oracleId = "89f43e27-790b-4ca1-8ba7-0882b31e0783",
    name = "Temple of Enlightenment",
    setCode = "FDN",
    collectorNumber = "698",
    scryfallId = "01f23c1a-a0fe-4520-88ed-045aa4044567",
    artist = "Piotr Dura",
    imageUri = "https://cards.scryfall.io/normal/front/0/1/01f23c1a-a0fe-4520-88ed-045aa4044567.jpg?1730491244",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
