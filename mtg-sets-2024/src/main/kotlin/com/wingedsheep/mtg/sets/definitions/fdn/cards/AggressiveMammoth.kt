package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Aggressive Mammoth reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AggressiveMammothReprint = Printing(
    oracleId = "032b5fc4-5aca-41b6-9bf6-2c1ed0018968",
    name = "Aggressive Mammoth",
    setCode = "FDN",
    collectorNumber = "551",
    artist = "Filip Burburan",
    imageUri = "https://cards.scryfall.io/normal/front/7/7/7724b978-999b-4654-96f3-58e28aa7cb34.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
