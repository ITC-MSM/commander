package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Steel Hellkite reprint in FDN. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Scars of Mirrodin (SOM); this file contributes only the FDN presentation row.
 */
val SteelHellkiteReprint = Printing(
    oracleId = "6e64f1e0-ddd6-4ee7-8797-086e234b02e8",
    name = "Steel Hellkite",
    setCode = "FDN",
    collectorNumber = "681",
    scryfallId = "931ad21b-a6bb-4a89-8d6f-80adfcf126c7",
    artist = "Jaime Jones",
    imageUri = "https://cards.scryfall.io/normal/front/9/3/931ad21b-a6bb-4a89-8d6f-80adfcf126c7.jpg?1783908904",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
