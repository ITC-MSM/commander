package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Kitesail reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Worldwake (`wwk`). This file
 * contributes only the MOM-specific presentation row — set, collector number, art.
 */
val KitesailReprint = Printing(
    oracleId = "b079f9db-974d-4525-a894-57b754ba9dcc",
    name = "Kitesail",
    setCode = "MOM",
    collectorNumber = "261",
    scryfallId = "fdc3bf6e-e011-4334-a142-c09941c6f213",
    artist = "Ben Hill",
    imageUri = "https://cards.scryfall.io/normal/front/f/d/fdc3bf6e-e011-4334-a142-c09941c6f213.jpg?1783916936",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
