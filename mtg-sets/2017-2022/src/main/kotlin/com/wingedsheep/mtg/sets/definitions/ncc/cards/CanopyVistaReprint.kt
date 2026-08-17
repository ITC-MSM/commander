package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Canopy Vista reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Battle for Zendikar's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val CanopyVistaReprint = Printing(
    oracleId = "dcb7e046-f01b-497c-88e5-57794eb30ce5",
    name = "Canopy Vista",
    setCode = "NCC",
    collectorNumber = "389",
    scryfallId = "79dffd83-05c8-4698-9677-5decb997e29f",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/7/9/79dffd83-05c8-4698-9677-5decb997e29f.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
