package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Infantry Veteran reprint in J22. Canonical CardDefinition lives in Visions (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.vis.cards.InfantryVeteran`.
 */
val InfantryVeteranReprint = Printing(
    oracleId = "42798e2b-9a5c-4f93-8f77-b7bb7a916d07",
    name = "Infantry Veteran",
    setCode = "J22",
    collectorNumber = "198",
    scryfallId = "0d42086a-74b3-4700-b070-95e1357ae527",
    artist = "Zoltan Boros",
    imageUri = "https://cards.scryfall.io/normal/front/0/d/0d42086a-74b3-4700-b070-95e1357ae527.jpg?1783919109",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
