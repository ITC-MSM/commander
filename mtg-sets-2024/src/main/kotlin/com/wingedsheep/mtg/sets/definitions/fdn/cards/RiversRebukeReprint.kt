package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * River's Rebuke reprint in FDN. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Ixalan (XLN); this file contributes only the FDN presentation row.
 */
val RiversRebukeReprint = Printing(
    oracleId = "c52cfb41-18f3-4e73-b5e7-d75baf74e578",
    name = "River's Rebuke",
    setCode = "FDN",
    collectorNumber = "595",
    scryfallId = "32ae984e-5d1e-4497-9a71-7406f78c09f3",
    artist = "Raymond Swanland",
    imageUri = "https://cards.scryfall.io/normal/front/3/2/32ae984e-5d1e-4497-9a71-7406f78c09f3.jpg?1782688748",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
