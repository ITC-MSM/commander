package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Arasta of the Endless Web reprint in CLB. The canonical CardDefinition lives in
 * Theros Beyond Death (`thb`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val ArastaOfTheEndlessWebReprint = Printing(
    oracleId = "695eea46-1535-48c5-bbb6-0b8379e77bfc",
    name = "Arasta of the Endless Web",
    setCode = "CLB",
    collectorNumber = "817",
    scryfallId = "558b6db8-4a54-4d25-98e7-e27efc1cab38",
    artist = "Sam Rowan",
    imageUri = "https://cards.scryfall.io/normal/front/5/5/558b6db8-4a54-4d25-98e7-e27efc1cab38.jpg?1783922411",
    releaseDate = "2022-06-10",
    rarity = Rarity.RARE,
    frameEffects = listOf("enchantment", "legendary"),
)
