package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ajani Goldmane reprint in Magic 2011 (M11). The canonical CardDefinition lives in
 * Lorwyn (`lrw`), the card's earliest real printing; this file contributes only
 * per-printing presentation data.
 */
val AjaniGoldmaneReprint = Printing(
    oracleId = "dfb5f660-fd8b-4b7b-934c-6a71cd182f15",
    name = "Ajani Goldmane",
    setCode = "M11",
    collectorNumber = "1",
    scryfallId = "2d911053-a026-4b20-ba2d-dbcc367c1413",
    artist = "Aleksi Briclot",
    imageUri = "https://cards.scryfall.io/normal/front/2/d/2d911053-a026-4b20-ba2d-dbcc367c1413.jpg?1783941838",
    releaseDate = "2010-07-16",
    rarity = Rarity.MYTHIC,
)
