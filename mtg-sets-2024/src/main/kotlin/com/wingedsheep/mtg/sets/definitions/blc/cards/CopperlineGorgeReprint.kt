package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Copperline Gorge reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CopperlineGorgeReprint = Printing(
    oracleId = "a05f641c-15c9-43dc-ae0d-1ea372fd33d5",
    name = "Copperline Gorge",
    setCode = "BLC",
    collectorNumber = "301",
    scryfallId = "c25daaa2-2a04-4fb0-8db3-6e564c26fadc",
    artist = "Yeong-Hao Han",
    imageUri = "https://cards.scryfall.io/normal/front/c/2/c25daaa2-2a04-4fb0-8db3-6e564c26fadc.jpg?1721429713",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
