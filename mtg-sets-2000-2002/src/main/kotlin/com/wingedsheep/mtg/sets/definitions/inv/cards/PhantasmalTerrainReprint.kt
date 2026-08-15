package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Phantasmal Terrain reprint in Invasion.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Alpha's
 * `cards/` package (earliest printing); this file contributes only the
 * INV-specific presentation row.
 */
val PhantasmalTerrainReprint = Printing(
    oracleId = "7dcbce46-2973-4a9f-93df-95ac41ce668a",
    name = "Phantasmal Terrain",
    setCode = "INV",
    collectorNumber = "65",
    scryfallId = "ea56a1bb-f52c-4c6b-a089-1f78600f3db0",
    artist = "Dana Knutson",
    imageUri = "https://cards.scryfall.io/normal/front/e/a/ea56a1bb-f52c-4c6b-a089-1f78600f3db0.jpg?1562942072",
    releaseDate = "2000-10-02",
    rarity = Rarity.COMMON,
)
