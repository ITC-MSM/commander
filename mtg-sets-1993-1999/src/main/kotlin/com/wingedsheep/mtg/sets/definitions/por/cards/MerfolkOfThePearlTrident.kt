package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Merfolk of the Pearl Trident reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MerfolkOfThePearlTridentReprint = Printing(
    oracleId = "218d9277-c179-4de3-9c7f-79b5a6d4fa38",
    name = "Merfolk of the Pearl Trident",
    setCode = "POR",
    collectorNumber = "60",
    artist = "DiTerlizzi",
    imageUri = "https://cards.scryfall.io/normal/front/1/2/126fec7a-4f36-49e5-a2d7-96deb7af856f.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
