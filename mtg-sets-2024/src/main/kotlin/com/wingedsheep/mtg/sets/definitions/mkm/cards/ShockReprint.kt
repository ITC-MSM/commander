package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Shock reprint in MKM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MKM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ShockReprint = Printing(
    oracleId = "a9d288b8-cdc1-4e55-a0c9-d6edfc95e65d",
    name = "Shock",
    setCode = "MKM",
    collectorNumber = "144",
    scryfallId = "298747bb-eb40-4b58-bb22-4ac2bc1d795c",
    artist = "Eric Wilkerson",
    imageUri = "https://cards.scryfall.io/normal/front/2/9/298747bb-eb40-4b58-bb22-4ac2bc1d795c.jpg?1706241920",
    releaseDate = "2024-02-09",
    rarity = Rarity.COMMON,
)
