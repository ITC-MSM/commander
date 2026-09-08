package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunforger reprint in Commander Legends. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `rav` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val SunforgerReprint = Printing(
    oracleId = "d1421070-a3cc-4af3-bf91-0f97372f4161",
    name = "Sunforger",
    setCode = "CMR",
    collectorNumber = "473",
    scryfallId = "dd3e42ee-ab13-460b-90fd-86e677abce4f",
    artist = "Darrell Riche",
    imageUri = "https://cards.scryfall.io/normal/front/d/d/dd3e42ee-ab13-460b-90fd-86e677abce4f.jpg?1783928686",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
)
