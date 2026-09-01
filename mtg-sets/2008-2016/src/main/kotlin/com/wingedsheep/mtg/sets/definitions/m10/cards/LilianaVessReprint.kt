package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Liliana Vess reprint in Magic 2010 (M10). The canonical CardDefinition lives in
 * Lorwyn (`lrw`), the card's earliest real printing; this file contributes only
 * per-printing presentation data.
 */
val LilianaVessReprint = Printing(
    oracleId = "a6c05941-2cfb-4dac-a7c0-ab808187eb7c",
    name = "Liliana Vess",
    setCode = "M10",
    collectorNumber = "102",
    scryfallId = "d6045789-a75b-4b7f-acde-0fdf7f3f262c",
    artist = "Aleksi Briclot",
    imageUri = "https://cards.scryfall.io/normal/front/d/6/d6045789-a75b-4b7f-acde-0fdf7f3f262c.jpg?1783942382",
    releaseDate = "2009-07-17",
    rarity = Rarity.MYTHIC,
)
