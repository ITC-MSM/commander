package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Prairie Stream reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Battle for Zendikar's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val PrairieStreamReprint = Printing(
    oracleId = "5330e24a-8568-446e-840a-594cd08bd1bc",
    name = "Prairie Stream",
    setCode = "NCC",
    collectorNumber = "421",
    scryfallId = "40e52996-863b-46ee-893a-6ecb29f23106",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/4/0/40e52996-863b-46ee-893a-6ecb29f23106.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
