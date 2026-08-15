package com.wingedsheep.mtg.sets.definitions.s99.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ogre Warrior reprint in S99.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * P02's `cards/` package (the card's earliest real printing). This file
 * contributes only the S99-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OgreWarriorReprint = Printing(
    oracleId = "31426e08-bfd1-44f1-8473-5e982cb8e841",
    name = "Ogre Warrior",
    setCode = "S99",
    collectorNumber = "113",
    artist = "Jeff Miracola",
    imageUri = "https://cards.scryfall.io/normal/front/3/7/3760dc16-6d36-4355-902e-44c1333bf049.jpg?1783946027",
    releaseDate = "1999-07-01",
    rarity = Rarity.COMMON,
)
