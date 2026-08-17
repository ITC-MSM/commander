package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Lightning Greaves reprint in CLB. The canonical CardDefinition lives in
 * Mirrodin (`mrd`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val LightningGreavesReprint = Printing(
    oracleId = "ca204b66-8d0c-431a-8d34-282f7c2d17da",
    name = "Lightning Greaves",
    setCode = "CLB",
    collectorNumber = "864",
    scryfallId = "8d9f47af-5929-44f4-bc6b-3ac7e521177d",
    artist = "Jeremy Jarvis",
    imageUri = "https://cards.scryfall.io/normal/front/8/d/8d9f47af-5929-44f4-bc6b-3ac7e521177d.jpg?1783922389",
    releaseDate = "2022-06-10",
    rarity = Rarity.UNCOMMON,
)
