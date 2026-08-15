package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Forgotten Ancient reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ForgottenAncientReprint = Printing(
    oracleId = "2ef75cf3-30dc-4852-a6d1-1d6391fad022",
    name = "Forgotten Ancient",
    setCode = "BLC",
    collectorNumber = "217",
    scryfallId = "c90b37d1-e708-494a-bc93-b1bfbaf29de5",
    artist = "Andrew Mar",
    imageUri = "https://cards.scryfall.io/normal/front/c/9/c90b37d1-e708-494a-bc93-b1bfbaf29de5.jpg?1721429268",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
