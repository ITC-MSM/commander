package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Glacial Fortress reprint in Ixalan. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m10` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val GlacialFortressReprint = Printing(
    oracleId = "027dd013-baa7-4111-b3c9-f4d1414e9c45",
    name = "Glacial Fortress",
    setCode = "XLN",
    collectorNumber = "255",
    scryfallId = "cef133d9-26d2-4a1e-8d6a-829f1067c169",
    artist = "James Paick",
    imageUri = "https://cards.scryfall.io/normal/front/c/e/cef133d9-26d2-4a1e-8d6a-829f1067c169.jpg?1783935697",
    releaseDate = "2017-09-29",
    rarity = Rarity.RARE,
)
