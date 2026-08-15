package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Aegis Turtle reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * IKO's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AegisTurtleReprint = Printing(
    oracleId = "f84c30e7-2ea8-43ff-9356-1f907558cfd9",
    name = "Aegis Turtle",
    setCode = "FDN",
    collectorNumber = "150",
    artist = "Milivoj Ćeran",
    imageUri = "https://cards.scryfall.io/normal/front/c/7/c7f2014a-fbc9-447c-a440-e06d01066bb9.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
