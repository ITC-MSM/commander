package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Driver of the Dead reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * AVR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DriverOfTheDeadReprint = Printing(
    oracleId = "7c179e54-3beb-4761-bddf-41fa98b082db",
    name = "Driver of the Dead",
    setCode = "FDN",
    collectorNumber = "605",
    artist = "James Ryman",
    imageUri = "https://cards.scryfall.io/normal/front/4/4/4443696a-0b9a-4081-91aa-800b5c4065d2.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
