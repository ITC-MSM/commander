package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cryptic Serpent reprint in J22. Canonical CardDefinition lives in Amonkhet (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.akh.cards.CrypticSerpent`.
 */
val CrypticSerpentReprint = Printing(
    oracleId = "0f26c04a-9bf7-4cbd-a95f-73ee4a3b1af1",
    name = "Cryptic Serpent",
    setCode = "J22",
    collectorNumber = "285",
    scryfallId = "cfc3e4cd-022d-48e4-abc9-b8ddfb0c8c5c",
    artist = "Lius Lasahido",
    imageUri = "https://cards.scryfall.io/normal/front/c/f/cfc3e4cd-022d-48e4-abc9-b8ddfb0c8c5c.jpg?1783919065",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
