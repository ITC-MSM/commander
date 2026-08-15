package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cursed Flesh reprint in Invasion. The canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in Exodus (`definitions/exo/cards/CursedFlesh.kt`); this file contributes only
 * presentation data for the Invasion printing.
 */
val CursedFleshReprint = Printing(
    oracleId = "96e244ef-21b9-4795-a881-c3db23b60cc9",
    name = "Cursed Flesh",
    setCode = "INV",
    collectorNumber = "98",
    scryfallId = "fb151ae8-9281-434d-ba8d-9ce34f0875eb",
    artist = "Chippy",
    imageUri = "https://cards.scryfall.io/normal/front/f/b/fb151ae8-9281-434d-ba8d-9ce34f0875eb.jpg?1562945657",
    releaseDate = "2000-10-02",
    rarity = Rarity.COMMON,
)
