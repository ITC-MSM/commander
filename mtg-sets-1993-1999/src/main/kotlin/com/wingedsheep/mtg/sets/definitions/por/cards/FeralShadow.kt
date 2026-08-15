package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Feral Shadow reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MIR's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FeralShadowReprint = Printing(
    oracleId = "cf26dcb4-e181-4ba5-bc03-b56f57032b85",
    name = "Feral Shadow",
    setCode = "POR",
    collectorNumber = "93",
    artist = "Colin MacNeil",
    imageUri = "https://cards.scryfall.io/normal/front/c/4/c46f4c00-6bf8-440b-9761-b17a0e36c27e.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
