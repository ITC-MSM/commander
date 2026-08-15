package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodfell Caves reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BloodfellCavesReprint = Printing(
    oracleId = "64e29bfc-9313-4e8c-808c-bc27f6b018a6",
    name = "Bloodfell Caves",
    setCode = "MOM",
    collectorNumber = "267",
    scryfallId = "85930f68-6f53-4921-9556-2887ac3abfd2",
    artist = "Jorge Jacinto",
    imageUri = "https://cards.scryfall.io/normal/front/8/5/85930f68-6f53-4921-9556-2887ac3abfd2.jpg?1682205829",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
