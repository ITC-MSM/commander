package com.wingedsheep.mtg.sets.definitions.msc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Doomsday reprint in Marvel Super Heroes Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the Weatherlight (`wth`) `cards/` package;
 * this file contributes only per-printing presentation data.
 */
val DoomsdayReprint = Printing(
    oracleId = "721eb5a2-d7cf-4db0-8013-ef3f596c52a5",
    name = "Doomsday",
    setCode = "MSC",
    collectorNumber = "796",
    scryfallId = "72cd296e-e550-46bf-9378-586a24c9b2a7",
    artist = "David Palumbo",
    imageUri = "https://cards.scryfall.io/normal/front/7/2/72cd296e-e550-46bf-9378-586a24c9b2a7.jpg?1783903013",
    releaseDate = "2026-06-26",
    rarity = Rarity.RARE,
)
