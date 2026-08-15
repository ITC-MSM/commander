package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Holy Day reprint in Invasion.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Legends'
 * `cards/` package; this file contributes only the Invasion-specific presentation row.
 */
val HolyDayReprint = Printing(
    oracleId = "98423a34-f044-4811-b288-56981d604b6e",
    name = "Holy Day",
    setCode = "INV",
    collectorNumber = "20",
    scryfallId = "aa91fd4e-4e1f-4cfa-b10f-456bd875238f",
    artist = "Pete Venters",
    imageUri = "https://cards.scryfall.io/normal/front/a/a/aa91fd4e-4e1f-4cfa-b10f-456bd875238f.jpg?1562929372",
    releaseDate = "2000-10-02",
    rarity = Rarity.COMMON,
)
