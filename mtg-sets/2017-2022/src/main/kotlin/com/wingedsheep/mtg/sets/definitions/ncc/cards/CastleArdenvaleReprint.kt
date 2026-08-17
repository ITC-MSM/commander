package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Castle Ardenvale reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `eld` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val CastleArdenvaleReprint = Printing(
    oracleId = "f8f4fc60-725d-46d8-8e8f-e68e00d20589",
    name = "Castle Ardenvale",
    setCode = "NCC",
    collectorNumber = "391",
    scryfallId = "eed204e1-45bd-4160-aaef-dde33bd9884a",
    artist = "Volkan Baǵa",
    imageUri = "https://cards.scryfall.io/normal/front/e/e/eed204e1-45bd-4160-aaef-dde33bd9884a.jpg?1783923205",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
