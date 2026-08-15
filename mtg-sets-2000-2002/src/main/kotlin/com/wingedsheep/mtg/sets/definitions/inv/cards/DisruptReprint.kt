package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Disrupt reprint in Invasion.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Weatherlight's
 * `cards/` package (Disrupt's earliest real-expansion printing); this file contributes
 * only the Invasion presentation row.
 */
val DisruptReprint = Printing(
    oracleId = "76347258-71d5-4874-adde-ff7aa932b808",
    name = "Disrupt",
    setCode = "INV",
    collectorNumber = "51",
    scryfallId = "c000a02f-6b7e-4925-a938-59e645e980d7",
    artist = "Paolo Parente",
    imageUri = "https://cards.scryfall.io/normal/front/c/0/c000a02f-6b7e-4925-a938-59e645e980d7.jpg?1562933600",
    releaseDate = "2000-10-02",
    rarity = Rarity.UNCOMMON,
)
