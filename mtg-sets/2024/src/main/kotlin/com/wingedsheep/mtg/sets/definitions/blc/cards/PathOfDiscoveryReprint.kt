package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Path of Discovery reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in
 * the RIX (Rivals of Ixalan) `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PathOfDiscoveryReprint = Printing(
    oracleId = "52714c3c-6821-44dd-88ba-813554df9914",
    name = "Path of Discovery",
    setCode = "BLC",
    collectorNumber = "231",
    scryfallId = "5f0dad60-90c2-41e7-bf85-4cb1343b4a9d",
    artist = "Howard Lyon",
    imageUri = "https://cards.scryfall.io/normal/front/5/f/5f0dad60-90c2-41e7-bf85-4cb1343b4a9d.jpg?1721429342",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
