package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Polluted Delta reprint in KTK.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ONS's `cards/` package (the card's earliest real printing). This file contributes
 * only the KTK-specific presentation row.
 */
val PollutedDeltaReprint = Printing(
    oracleId = "ef86989d-ce80-4e55-aece-7d11710eeffa",
    name = "Polluted Delta",
    setCode = "KTK",
    collectorNumber = "239",
    scryfallId = "ff2f5f58-9a95-4ca6-93a0-813738f0072f",
    artist = "Vincent Proce",
    imageUri = "https://cards.scryfall.io/normal/front/f/f/ff2f5f58-9a95-4ca6-93a0-813738f0072f.jpg?1707235020",
    releaseDate = "2014-09-26",
    rarity = Rarity.RARE,
)
