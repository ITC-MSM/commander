package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Doomed Dissenter reprint in J22. Canonical CardDefinition lives in Amonkhet (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.akh.cards.DoomedDissenter`.
 */
val DoomedDissenterReprint = Printing(
    oracleId = "11cb509d-21af-48f1-b355-135ebd3e4bd1",
    name = "Doomed Dissenter",
    setCode = "J22",
    collectorNumber = "402",
    scryfallId = "a53d3ce5-3e71-44f1-ab0e-98d399287722",
    artist = "Tony Foti",
    imageUri = "https://cards.scryfall.io/normal/front/a/5/a53d3ce5-3e71-44f1-ab0e-98d399287722.jpg?1783919012",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
