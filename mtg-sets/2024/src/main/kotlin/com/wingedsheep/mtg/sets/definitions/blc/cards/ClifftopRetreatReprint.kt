package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Clifftop Retreat reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ClifftopRetreatReprint = Printing(
    oracleId = "d7faa3c8-46cf-46b2-bfa4-89000307cf18",
    name = "Clifftop Retreat",
    setCode = "BLC",
    collectorNumber = "300",
    scryfallId = "42ca7627-965e-4594-a871-d8d137d6b9a9",
    artist = "Christine Choi",
    imageUri = "https://cards.scryfall.io/normal/front/4/2/42ca7627-965e-4594-a871-d8d137d6b9a9.jpg?1721429707",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
