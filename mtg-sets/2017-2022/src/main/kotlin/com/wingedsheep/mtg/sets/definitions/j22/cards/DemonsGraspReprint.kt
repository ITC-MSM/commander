package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Demon's Grasp reprint in J22. Canonical CardDefinition lives in Battle for Zendikar (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.bfz.cards.DemonsGrasp`.
 */
val DemonsGraspReprint = Printing(
    oracleId = "13228a22-d63a-4952-a482-4f84da696e88",
    name = "Demon's Grasp",
    setCode = "J22",
    collectorNumber = "400",
    scryfallId = "c305f55c-6bcf-464b-aa80-e227bf30c9ea",
    artist = "David Gaillet",
    imageUri = "https://cards.scryfall.io/normal/front/c/3/c305f55c-6bcf-464b-aa80-e227bf30c9ea.jpg?1783919013",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
