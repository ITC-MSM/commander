package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Primeval Bounty reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in
 * the M14 (Magic 2014) `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PrimevalBountyReprint = Printing(
    oracleId = "f633c16d-d943-421c-ad18-af35db9ec9fc",
    name = "Primeval Bounty",
    setCode = "BLC",
    collectorNumber = "232",
    scryfallId = "5e0577c4-fc62-4222-9e32-b48c58e47462",
    artist = "Christine Choi",
    imageUri = "https://cards.scryfall.io/normal/front/5/e/5e0577c4-fc62-4222-9e32-b48c58e47462.jpg?1721429344",
    releaseDate = "2024-08-02",
    rarity = Rarity.MYTHIC,
)
