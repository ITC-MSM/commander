package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rise from the Tides reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * SOI's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RiseFromTheTidesReprint = Printing(
    oracleId = "390b862a-2a85-44bd-832c-f5bbd3eb4ea0",
    name = "Rise from the Tides",
    setCode = "INR",
    collectorNumber = "82",
    artist = "Dave Kendall",
    imageUri = "https://cards.scryfall.io/normal/front/a/1/a16fccff-ca97-4416-b42f-86d314b1d0af.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.UNCOMMON,
)
