package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Muldrotha, the Gravetide reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MuldrothaTheGravetideReprint = Printing(
    oracleId = "e4625704-1d52-44e4-804f-2f45644d76ac",
    name = "Muldrotha, the Gravetide",
    setCode = "FDN",
    collectorNumber = "243",
    scryfallId = "51710b19-68e3-4853-901f-e618bde61161",
    artist = "Jason Rainville",
    imageUri = "https://cards.scryfall.io/normal/front/5/1/51710b19-68e3-4853-901f-e618bde61161.jpg?1730489512",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)
