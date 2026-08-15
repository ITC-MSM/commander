package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Baird, Steward of Argive reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BairdStewardOfArgiveReprint = Printing(
    oracleId = "69aa6809-5468-413a-94c9-562e6de93957",
    name = "Baird, Steward of Argive",
    setCode = "BLC",
    collectorNumber = "135",
    scryfallId = "5165b4fa-ce48-49c3-9cb3-dc6ece8c0f08",
    artist = "Christine Choi",
    imageUri = "https://cards.scryfall.io/normal/front/5/1/5165b4fa-ce48-49c3-9cb3-dc6ece8c0f08.jpg?1721428833",
    releaseDate = "2024-08-02",
    rarity = Rarity.UNCOMMON,
)
