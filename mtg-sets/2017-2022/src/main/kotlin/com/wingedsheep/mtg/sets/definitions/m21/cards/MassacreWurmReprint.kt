package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Massacre Wurm reprints in Core Set 2021.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in MBS's `cards/` package
 * (the card's earliest real printing). These files contribute only M21 presentation rows —
 * the regular printing (#114) and the alternate-art variant (#316).
 */
val MassacreWurmReprint = Printing(
    oracleId = "93cf50cf-0ecc-4d3e-abea-778c1ebacec4",
    name = "Massacre Wurm",
    setCode = "M21",
    collectorNumber = "114",
    scryfallId = "e3185b01-0d91-4927-98e9-bc9df6c20917",
    artist = "Jason Chan",
    imageUri = "https://cards.scryfall.io/normal/front/e/3/e3185b01-0d91-4927-98e9-bc9df6c20917.jpg?1782706944",
    releaseDate = "2020-07-03",
    rarity = Rarity.MYTHIC,
)

val MassacreWurmAltArtReprint = Printing(
    oracleId = "93cf50cf-0ecc-4d3e-abea-778c1ebacec4",
    name = "Massacre Wurm",
    setCode = "M21",
    collectorNumber = "316",
    scryfallId = "a0523c0a-b9b6-4946-b90d-ed8e13cf1915",
    artist = "Kekai Kotaki",
    imageUri = "https://cards.scryfall.io/normal/front/a/0/a0523c0a-b9b6-4946-b90d-ed8e13cf1915.jpg?1782706814",
    releaseDate = "2020-07-03",
    rarity = Rarity.MYTHIC,
)
