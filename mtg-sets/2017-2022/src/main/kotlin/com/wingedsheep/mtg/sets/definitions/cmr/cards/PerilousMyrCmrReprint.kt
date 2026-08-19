package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Perilous Myr reprint in Commander Legends. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives
 * in another set's `cards/` package; this file contributes only presentation data.
 */
val PerilousMyrCmrReprint = Printing(
    oracleId = "f1e8f746-1837-4ea8-9a07-e57946883353",
    name = "Perilous Myr",
    setCode = "CMR",
    collectorNumber = "330",
    scryfallId = "5a15c8ef-04ad-4aab-a7f1-c7a90c10eb50",
    artist = "Jason Felix",
    imageUri = "https://cards.scryfall.io/normal/front/5/a/5a15c8ef-04ad-4aab-a7f1-c7a90c10eb50.jpg?1783928750",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
