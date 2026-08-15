package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Temple of Triumph reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TempleOfTriumphReprint = Printing(
    oracleId = "6f0d94d9-64bb-4175-83bc-301e8f79f54f",
    name = "Temple of Triumph",
    setCode = "FDN",
    collectorNumber = "705",
    scryfallId = "d2f58450-838d-4404-a68f-159e82b0e58d",
    artist = "Piotr Dura",
    imageUri = "https://cards.scryfall.io/normal/front/d/2/d2f58450-838d-4404-a68f-159e82b0e58d.jpg?1730491275",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
