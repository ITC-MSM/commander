package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * River's Rebuke reprint in New Capenna Commander (NCC). Canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Ixalan (XLN); this file contributes only the
 * NCC presentation row.
 */
val RiversRebukeReprint = Printing(
    oracleId = "c52cfb41-18f3-4e73-b5e7-d75baf74e578",
    name = "River's Rebuke",
    setCode = "NCC",
    collectorNumber = "231",
    scryfallId = "fc2a70b1-bce6-43c3-94e1-ab9a7bb2638a",
    artist = "Raymond Swanland",
    imageUri = "https://cards.scryfall.io/normal/front/f/c/fc2a70b1-bce6-43c3-94e1-ab9a7bb2638a.jpg?1782701805",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
