package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Stalking Tiger reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MIR's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StalkingTigerReprint = Printing(
    oracleId = "e6af1429-276f-41a8-88d0-062661dc0cd4",
    name = "Stalking Tiger",
    setCode = "POR",
    collectorNumber = "186",
    artist = "Colin MacNeil",
    imageUri = "https://cards.scryfall.io/normal/front/c/b/cbc78337-2d1a-4a1d-8630-fcf7a7f6abce.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
