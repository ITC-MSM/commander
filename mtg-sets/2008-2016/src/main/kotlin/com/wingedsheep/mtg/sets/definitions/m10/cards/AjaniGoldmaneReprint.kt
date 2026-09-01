package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ajani Goldmane reprint in Magic 2010 (M10). The canonical CardDefinition lives in
 * Lorwyn (`lrw`), the card's earliest real printing; this file contributes only
 * per-printing presentation data.
 */
val AjaniGoldmaneReprint = Printing(
    oracleId = "dfb5f660-fd8b-4b7b-934c-6a71cd182f15",
    name = "Ajani Goldmane",
    setCode = "M10",
    collectorNumber = "1",
    scryfallId = "46c50891-af21-4427-a495-0e66aef54809",
    artist = "Aleksi Briclot",
    imageUri = "https://cards.scryfall.io/normal/front/4/6/46c50891-af21-4427-a495-0e66aef54809.jpg?1783942405",
    releaseDate = "2009-07-17",
    rarity = Rarity.MYTHIC,
)
