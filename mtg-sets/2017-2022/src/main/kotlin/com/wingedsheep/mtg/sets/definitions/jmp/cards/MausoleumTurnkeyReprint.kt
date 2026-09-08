package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mausoleum Turnkey reprint in Jumpstart. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `rav` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val MausoleumTurnkeyReprint = Printing(
    oracleId = "d4a61685-7149-4d14-bc17-85b060945687",
    name = "Mausoleum Turnkey",
    setCode = "JMP",
    collectorNumber = "255",
    scryfallId = "f7b6e993-1988-4b6d-970e-be71d95cf21a",
    artist = "Darrell Riche",
    imageUri = "https://cards.scryfall.io/normal/front/f/7/f7b6e993-1988-4b6d-970e-be71d95cf21a.jpg?1783930417",
    releaseDate = "2020-07-17",
    rarity = Rarity.UNCOMMON,
)
