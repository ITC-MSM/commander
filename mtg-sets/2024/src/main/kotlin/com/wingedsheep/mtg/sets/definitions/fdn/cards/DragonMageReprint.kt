package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dragon Mage reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DragonMageReprint = Printing(
    oracleId = "eab71e4e-27c3-4c41-b95f-259c1d14b97a",
    name = "Dragon Mage",
    setCode = "FDN",
    collectorNumber = "621",
    scryfallId = "5525cfde-a92b-4fb5-8f15-a05efebecd3d",
    artist = "Matthew D. Wilson",
    imageUri = "https://cards.scryfall.io/normal/front/5/5/5525cfde-a92b-4fb5-8f15-a05efebecd3d.jpg?1730490953",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
