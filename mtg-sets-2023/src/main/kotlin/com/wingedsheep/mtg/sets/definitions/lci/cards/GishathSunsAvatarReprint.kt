package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gishath, Sun's Avatar reprint in LCI.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T)
 * lives in the Ixalan (XLN) set's `cards/` package. This file contributes only
 * the LCI-specific presentation row — set, collector number, art — picked up
 * automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's
 * `printings`.
 */
val GishathSunsAvatarReprint = Printing(
    oracleId = "a311daab-40dc-4433-9ed8-feab7c558bbf",
    name = "Gishath, Sun's Avatar",
    setCode = "LCI",
    collectorNumber = "229",
    scryfallId = "bc4a65de-23b5-48f0-b8b7-94608eaced3e",
    artist = "Zack Stella",
    imageUri = "https://cards.scryfall.io/normal/front/b/c/bc4a65de-23b5-48f0-b8b7-94608eaced3e.jpg?1699044539",
    releaseDate = "2023-11-17",
    rarity = Rarity.MYTHIC,
)
