package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Stone Golem reprint in M20.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M11's `cards/` package (the card's earliest real printing). This file
 * contributes only the M20-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StoneGolemReprint = Printing(
    oracleId = "7cfb32af-ac59-4459-ac9e-2762488e9180",
    name = "Stone Golem",
    setCode = "M20",
    collectorNumber = "240",
    artist = "Martina Pilcerova",
    imageUri = "https://cards.scryfall.io/normal/front/1/b/1b4de70a-729b-4566-b6f3-c76f551405a5.jpg?1783932939",
    releaseDate = "2019-07-12",
    rarity = Rarity.COMMON,
)
