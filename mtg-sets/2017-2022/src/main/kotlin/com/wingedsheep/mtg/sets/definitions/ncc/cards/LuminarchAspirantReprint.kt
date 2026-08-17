package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Luminarch Aspirant reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Zendikar Rising's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val LuminarchAspirantReprint = Printing(
    oracleId = "cb9994b9-924b-4e10-9075-9cfbec88f2bf",
    name = "Luminarch Aspirant",
    setCode = "NCC",
    collectorNumber = "205",
    scryfallId = "dcd27fa3-f6b6-4137-9b6c-4cba7187664c",
    artist = "Mads Ahm",
    imageUri = "https://cards.scryfall.io/normal/front/d/c/dcd27fa3-f6b6-4137-9b6c-4cba7187664c.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
