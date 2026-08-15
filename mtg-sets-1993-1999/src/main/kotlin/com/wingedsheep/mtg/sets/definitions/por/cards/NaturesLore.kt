package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Nature's Lore reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ICE's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val NaturesLoreReprint = Printing(
    oracleId = "78826359-fe63-44ad-adc4-a17ffcd710e4",
    name = "Nature's Lore",
    setCode = "POR",
    collectorNumber = "178",
    artist = "Terese Nielsen",
    imageUri = "https://cards.scryfall.io/normal/front/d/5/d5242227-d033-4e03-b1e6-b334b17bb158.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
