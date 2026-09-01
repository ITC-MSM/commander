package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Liliana Vess reprint in Magic 2015 (M15). The canonical CardDefinition lives in
 * Lorwyn (`lrw`), the card's earliest real printing; this file contributes only
 * per-printing presentation data.
 */
val LilianaVessReprint = Printing(
    oracleId = "a6c05941-2cfb-4dac-a7c0-ab808187eb7c",
    name = "Liliana Vess",
    setCode = "M15",
    collectorNumber = "103",
    scryfallId = "6087f146-f468-472a-b248-fc7386ea3e63",
    artist = "Aleksi Briclot",
    imageUri = "https://cards.scryfall.io/normal/front/6/0/6087f146-f468-472a-b248-fc7386ea3e63.jpg?1783939183",
    releaseDate = "2014-07-18",
    rarity = Rarity.MYTHIC,
)
