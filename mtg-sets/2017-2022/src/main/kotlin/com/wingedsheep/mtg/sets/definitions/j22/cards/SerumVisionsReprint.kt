package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Serum Visions reprint in J22. Canonical CardDefinition lives in Fifth Dawn (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.5dn.cards.SerumVisions`.
 */
val SerumVisionsReprint = Printing(
    oracleId = "56956afd-db53-4542-816b-490c8b0bbcf7",
    name = "Serum Visions",
    setCode = "J22",
    collectorNumber = "345",
    scryfallId = "a44d8329-fe4c-4102-a5ee-3c058b84e315",
    artist = "Izzy",
    imageUri = "https://cards.scryfall.io/normal/front/a/4/a44d8329-fe4c-4102-a5ee-3c058b84e315.jpg?1783919045",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
