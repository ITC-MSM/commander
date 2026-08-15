package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Frenzied Raptor reprint in IKO.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * XLN's `cards/` package (the card's earliest real printing). This file
 * contributes only the IKO-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FrenziedRaptorReprint = Printing(
    oracleId = "655c59d5-8666-476c-b494-411fa2e243a7",
    name = "Frenzied Raptor",
    setCode = "IKO",
    collectorNumber = "120",
    artist = "Jonathan Kuo",
    imageUri = "https://cards.scryfall.io/normal/front/5/f/5fb22ac0-3863-4165-8c93-f2ec1775474f.jpg?1783931050",
    releaseDate = "2020-04-24",
    rarity = Rarity.COMMON,
)
