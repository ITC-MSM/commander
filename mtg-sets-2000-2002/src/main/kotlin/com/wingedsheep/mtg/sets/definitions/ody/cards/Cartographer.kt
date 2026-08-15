package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cartographer reprint in ODY.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * EXO's `cards/` package (the card's earliest real printing). This file contributes only
 * the ODY-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CartographerReprint = Printing(
    oracleId = "50d33663-5d83-4e3c-9ed7-074fe29a9fc8",
    name = "Cartographer",
    setCode = "ODY",
    collectorNumber = "232",
    artist = "Donato Giancola",
    imageUri = "https://cards.scryfall.io/normal/front/8/2/8241680d-6453-44ac-ab0f-3e7ebdd31e89.jpg",
    releaseDate = "2001-10-01",
    rarity = Rarity.COMMON,
)
