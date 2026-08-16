package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Maalfeld Twins reprint in J22. Canonical CardDefinition lives in Avacyn Restored (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.avr.cards.MaalfeldTwins`.
 */
val MaalfeldTwinsReprint = Printing(
    oracleId = "dda4b515-49c0-43fe-9f6a-36defa326bb1",
    name = "Maalfeld Twins",
    setCode = "J22",
    collectorNumber = "438",
    scryfallId = "7c8cce8a-43b4-42aa-abbd-0835583e74bd",
    artist = "Mike Sass",
    imageUri = "https://cards.scryfall.io/normal/front/7/c/7c8cce8a-43b4-42aa-abbd-0835583e74bd.jpg?1783918995",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
