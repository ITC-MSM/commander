package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Smoldering Marsh reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Battle for Zendikar's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val SmolderingMarshReprint = Printing(
    oracleId = "390f1b56-264e-4336-83be-dc1fe79bfdcf",
    name = "Smoldering Marsh",
    setCode = "NCC",
    collectorNumber = "428",
    scryfallId = "e0e4a509-09bb-41e5-a37a-53d98c7f2e04",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/e/0/e0e4a509-09bb-41e5-a37a-53d98c7f2e04.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
