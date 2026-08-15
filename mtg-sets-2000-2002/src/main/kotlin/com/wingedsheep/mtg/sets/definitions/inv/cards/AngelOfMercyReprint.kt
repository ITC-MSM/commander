package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Angel of Mercy reprint in Invasion.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Portal Second Age's
 * `cards/` package; this file contributes only the Invasion-specific presentation row.
 */
val AngelOfMercyReprint = Printing(
    oracleId = "a2daaf32-dbfe-4618-892e-0da24f63a44a",
    name = "Angel of Mercy",
    setCode = "INV",
    collectorNumber = "2",
    scryfallId = "5b6de688-685f-4389-be35-a472ada988e1",
    artist = "Mark Tedin",
    imageUri = "https://cards.scryfall.io/normal/front/5/b/5b6de688-685f-4389-be35-a472ada988e1.jpg?1562913560",
    releaseDate = "2000-10-02",
    rarity = Rarity.UNCOMMON,
)
