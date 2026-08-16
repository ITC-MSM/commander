package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Graf Harvest reprint in J22. Canonical CardDefinition lives in Eldritch Moon (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.emn.cards.GrafHarvest`.
 */
val GrafHarvestReprint = Printing(
    oracleId = "d3ba6922-c2f7-45ab-87a3-d4bbd770d1ba",
    name = "Graf Harvest",
    setCode = "J22",
    collectorNumber = "421",
    scryfallId = "83f2413f-987f-4eab-96c1-f662821546a5",
    artist = "Lake Hurwitz",
    imageUri = "https://cards.scryfall.io/normal/front/8/3/83f2413f-987f-4eab-96c1-f662821546a5.jpg?1783919002",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
