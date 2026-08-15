package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Temple of Mystery reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TempleOfMysteryReprint = Printing(
    oracleId = "7e26f0b7-20e6-46d5-8130-d98c14d6aa29",
    name = "Temple of Mystery",
    setCode = "FDN",
    collectorNumber = "702",
    scryfallId = "bd581f1d-d8ba-47f9-b10b-b7b6d46239ce",
    artist = "Piotr Dura",
    imageUri = "https://cards.scryfall.io/normal/front/b/d/bd581f1d-d8ba-47f9-b10b-b7b6d46239ce.jpg?1730491259",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
