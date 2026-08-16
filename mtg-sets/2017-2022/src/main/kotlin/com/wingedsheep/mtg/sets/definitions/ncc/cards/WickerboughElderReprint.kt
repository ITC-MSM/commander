package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wickerbough Elder reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Eventide's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val WickerboughElderReprint = Printing(
    oracleId = "3d794629-9014-460a-8d3b-785eadc1cdb5",
    name = "Wickerbough Elder",
    setCode = "NCC",
    collectorNumber = "320",
    scryfallId = "3a5832cc-f9f6-4881-99dd-c0728a52cabe",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/3/a/3a5832cc-f9f6-4881-99dd-c0728a52cabe.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.COMMON,
)
