package com.wingedsheep.mtg.sets.definitions.c16.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunforger reprint in Commander 2016. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `rav` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val SunforgerReprint = Printing(
    oracleId = "d1421070-a3cc-4af3-bf91-0f97372f4161",
    name = "Sunforger",
    setCode = "C16",
    collectorNumber = "275",
    scryfallId = "0f9a9654-edf1-486f-a002-57418f03de8e",
    artist = "Darrell Riche",
    imageUri = "https://cards.scryfall.io/normal/front/0/f/0f9a9654-edf1-486f-a002-57418f03de8e.jpg?1783937033",
    releaseDate = "2016-11-11",
    rarity = Rarity.RARE,
)
