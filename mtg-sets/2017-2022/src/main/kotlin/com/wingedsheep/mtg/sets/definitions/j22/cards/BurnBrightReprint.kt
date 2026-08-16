package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Burn Bright reprint in J22. Canonical CardDefinition lives in Ravnica Allegiance (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.rna.cards.BurnBright`.
 */
val BurnBrightReprint = Printing(
    oracleId = "ed14c7fa-632c-47ee-9db0-8c492771121a",
    name = "Burn Bright",
    setCode = "J22",
    collectorNumber = "508",
    scryfallId = "8836ea09-9136-4f97-9b38-1aeb9c155d03",
    artist = "Scott Murphy",
    imageUri = "https://cards.scryfall.io/normal/front/8/8/8836ea09-9136-4f97-9b38-1aeb9c155d03.jpg?1783918958",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
