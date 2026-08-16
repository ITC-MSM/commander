package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Lightning Greaves reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Mirrodin's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val LightningGreavesReprint = Printing(
    oracleId = "ca204b66-8d0c-431a-8d34-282f7c2d17da",
    name = "Lightning Greaves",
    setCode = "NCC",
    collectorNumber = "371",
    scryfallId = "205430f6-2d00-4dce-98c7-3d24a8ae73fc",
    artist = "Jeremy Jarvis",
    imageUri = "https://cards.scryfall.io/normal/front/2/0/205430f6-2d00-4dce-98c7-3d24a8ae73fc.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
