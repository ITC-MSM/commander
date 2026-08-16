package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Thragtusk reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Magic 2013's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val ThragtuskReprint = Printing(
    oracleId = "0dd0e91a-d16b-4718-8d11-1a3fcf8e0753",
    name = "Thragtusk",
    setCode = "NCC",
    collectorNumber = "316",
    scryfallId = "beda7acd-e970-4222-9577-5133765d6052",
    artist = "Nils Hamm",
    imageUri = "https://cards.scryfall.io/normal/front/b/e/beda7acd-e970-4222-9577-5133765d6052.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
