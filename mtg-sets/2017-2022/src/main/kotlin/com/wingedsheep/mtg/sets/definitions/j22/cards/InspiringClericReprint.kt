package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Inspiring Cleric reprint in J22. Canonical CardDefinition lives in Ixalan (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.xln.cards.InspiringCleric`.
 */
val InspiringClericReprint = Printing(
    oracleId = "65e19aec-e8ea-412f-a239-bd8d1b78249c",
    name = "Inspiring Cleric",
    setCode = "J22",
    collectorNumber = "199",
    scryfallId = "adc56f47-ae0a-498b-8730-2096937887e5",
    artist = "Randy Gallegos",
    imageUri = "https://cards.scryfall.io/normal/front/a/d/adc56f47-ae0a-498b-8730-2096937887e5.jpg?1783919108",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
