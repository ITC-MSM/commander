package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Flooded Strand reprint in KTK.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ONS's `cards/` package (the card's earliest real printing). This file contributes
 * only the KTK-specific presentation row.
 */
val FloodedStrandReprint = Printing(
    oracleId = "f3c7af78-a77d-4134-82a2-a5ce84285a84",
    name = "Flooded Strand",
    setCode = "KTK",
    collectorNumber = "233",
    scryfallId = "8c2996d9-3287-4480-8c04-7a378e37e3cf",
    artist = "Andreas Rocha",
    imageUri = "https://cards.scryfall.io/normal/front/8/c/8c2996d9-3287-4480-8c04-7a378e37e3cf.jpg?1707237513",
    releaseDate = "2014-09-26",
    rarity = Rarity.RARE,
)
