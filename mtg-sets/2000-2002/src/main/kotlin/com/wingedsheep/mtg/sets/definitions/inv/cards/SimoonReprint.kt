package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Simoon reprint in Invasion.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Visions'
 * `cards/` package; this file contributes only the Invasion-specific presentation row.
 */
val SimoonReprint = Printing(
    oracleId = "128e201a-f520-4917-a1a4-2f3836f1f92d",
    name = "Simoon",
    setCode = "INV",
    collectorNumber = "272",
    scryfallId = "84b1930d-2e4b-472f-98a9-008fd632f3be",
    artist = "Tony Szczudlo",
    imageUri = "https://cards.scryfall.io/normal/front/8/4/84b1930d-2e4b-472f-98a9-008fd632f3be.jpg?1562921826",
    releaseDate = "2000-10-02",
    rarity = Rarity.UNCOMMON,
)
