package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Darksteel Colossus reprint in Magic 2010.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in DST's `cards/` package (the
 * card's earliest real printing). This file contributes only the M10-specific presentation row.
 */
val DarksteelColossusReprint = Printing(
    oracleId = "1b09d0cf-403c-4a15-aeee-602a1bdaf0c1",
    name = "Darksteel Colossus",
    setCode = "M10",
    collectorNumber = "208",
    scryfallId = "5a3aa1ce-8757-4541-8ac7-1e7e203b60fc",
    artist = "Carl Critchlow",
    imageUri = "https://cards.scryfall.io/normal/front/5/a/5a3aa1ce-8757-4541-8ac7-1e7e203b60fc.jpg?1783942356",
    releaseDate = "2009-07-17",
    rarity = Rarity.MYTHIC,
)
