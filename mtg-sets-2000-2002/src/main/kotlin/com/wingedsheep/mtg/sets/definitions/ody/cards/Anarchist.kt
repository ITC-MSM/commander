package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Anarchist reprint in ODY.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * EXO's `cards/` package (the card's earliest real printing). This file contributes only
 * the ODY-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AnarchistReprint = Printing(
    oracleId = "82b9ad93-df75-4417-9746-798db8fe6ba0",
    name = "Anarchist",
    setCode = "ODY",
    collectorNumber = "173",
    artist = "Greg Hildebrandt",
    imageUri = "https://cards.scryfall.io/normal/front/5/5/5514627a-b24c-48c9-9a5a-5a375adcdf62.jpg",
    releaseDate = "2001-10-01",
    rarity = Rarity.COMMON,
)
