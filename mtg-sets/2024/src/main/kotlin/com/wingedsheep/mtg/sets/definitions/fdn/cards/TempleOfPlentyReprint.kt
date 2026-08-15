package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Temple of Plenty reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TempleOfPlentyReprint = Printing(
    oracleId = "e521322b-0e83-458c-8936-7021a80ee279",
    name = "Temple of Plenty",
    setCode = "FDN",
    collectorNumber = "703",
    scryfallId = "69762774-c8e1-42de-90b3-5a4d50f5d84d",
    artist = "Chris Ostrowski",
    imageUri = "https://cards.scryfall.io/normal/front/6/9/69762774-c8e1-42de-90b3-5a4d50f5d84d.jpg?1730491267",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
