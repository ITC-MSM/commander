package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Arasta of the Endless Web reprint in NCC. The canonical CardDefinition lives in
 * Theros Beyond Death (`thb`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val ArastaOfTheEndlessWebReprint = Printing(
    oracleId = "695eea46-1535-48c5-bbb6-0b8379e77bfc",
    name = "Arasta of the Endless Web",
    setCode = "NCC",
    collectorNumber = "279",
    scryfallId = "e905a310-02d5-4a86-bb58-2bb3502edba2",
    artist = "Sam Rowan",
    imageUri = "https://cards.scryfall.io/normal/front/e/9/e905a310-02d5-4a86-bb58-2bb3502edba2.jpg?1783923254",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
    frameEffects = listOf("legendary", "enchantment"),
)
