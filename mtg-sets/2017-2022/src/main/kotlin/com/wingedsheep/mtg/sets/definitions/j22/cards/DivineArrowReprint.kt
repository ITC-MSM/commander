package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Divine Arrow reprint in J22. Canonical CardDefinition lives in War of the Spark (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.war.cards.DivineArrow`.
 */
val DivineArrowReprint = Printing(
    oracleId = "6add505a-9c0f-42e6-898d-be3194c3df33",
    name = "Divine Arrow",
    setCode = "J22",
    collectorNumber = "176",
    scryfallId = "80b54945-444f-45db-bffa-0d844291f579",
    artist = "Slawomir Maniak",
    imageUri = "https://cards.scryfall.io/normal/front/8/0/80b54945-444f-45db-bffa-0d844291f579.jpg?1783919118",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
