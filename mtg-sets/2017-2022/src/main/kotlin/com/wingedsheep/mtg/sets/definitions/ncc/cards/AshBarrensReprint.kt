package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ash Barrens reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Commander 2016's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val AshBarrensReprint = Printing(
    oracleId = "58257464-278e-45fa-8e0b-bcd9a7500bc1",
    name = "Ash Barrens",
    setCode = "NCC",
    collectorNumber = "386",
    scryfallId = "5233b31f-a179-4ae9-95a2-2ff75b374edf",
    artist = "Jonas De Ro",
    imageUri = "https://cards.scryfall.io/normal/front/5/2/5233b31f-a179-4ae9-95a2-2ff75b374edf.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
