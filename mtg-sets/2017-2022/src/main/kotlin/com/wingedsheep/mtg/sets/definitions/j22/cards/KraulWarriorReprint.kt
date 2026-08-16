package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Kraul Warrior reprint in J22. Canonical CardDefinition lives in Dragon's Maze (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.dgm.cards.KraulWarrior`.
 */
val KraulWarriorReprint = Printing(
    oracleId = "b05d000d-39b4-4db5-bd32-9c77a7434127",
    name = "Kraul Warrior",
    setCode = "J22",
    collectorNumber = "684",
    scryfallId = "6fd7b87d-cd5c-45de-a7bd-e834337f3400",
    artist = "David Rapoza",
    imageUri = "https://cards.scryfall.io/normal/front/6/f/6fd7b87d-cd5c-45de-a7bd-e834337f3400.jpg?1783918862",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
