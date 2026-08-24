package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cryptic Serpent reprint in JMP. Canonical CardDefinition lives in Amonkhet (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.akh.cards.CrypticSerpent`.
 */
val CrypticSerpentReprint = Printing(
    oracleId = "0f26c04a-9bf7-4cbd-a95f-73ee4a3b1af1",
    name = "Cryptic Serpent",
    setCode = "JMP",
    collectorNumber = "146",
    scryfallId = "6a636f74-3bac-4b88-a24f-32a66a94e340",
    artist = "Lius Lasahido",
    imageUri = "https://cards.scryfall.io/normal/front/6/a/6a636f74-3bac-4b88-a24f-32a66a94e340.jpg?1783930457",
    releaseDate = "2020-07-17",
    rarity = Rarity.UNCOMMON,
)
