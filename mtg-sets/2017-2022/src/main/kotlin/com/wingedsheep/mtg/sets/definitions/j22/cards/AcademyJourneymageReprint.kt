package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Academy Journeymage reprint in J22. Canonical CardDefinition lives in Dominaria (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.dom.cards.AcademyJourneymage`.
 */
val AcademyJourneymageReprint = Printing(
    oracleId = "b400700c-1d82-4721-a166-56f88ba6ad19",
    name = "Academy Journeymage",
    setCode = "J22",
    collectorNumber = "267",
    scryfallId = "6a88507f-8089-42a6-a722-d07d04a59295",
    artist = "Magali Villeneuve",
    imageUri = "https://cards.scryfall.io/normal/front/6/a/6a88507f-8089-42a6-a722-d07d04a59295.jpg?1783919075",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
