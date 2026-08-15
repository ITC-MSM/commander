package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Maritime Guard reprint in ORI.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M11's `cards/` package (the card's earliest real printing). This file
 * contributes only the ORI-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MaritimeGuardReprint = Printing(
    oracleId = "0bb1c704-47f1-4d0c-8158-cfcd0b5e4d06",
    name = "Maritime Guard",
    setCode = "ORI",
    collectorNumber = "63",
    artist = "Allen Williams",
    imageUri = "https://cards.scryfall.io/normal/front/1/0/1008ff1b-7fb0-4570-b23e-9fda14b97640.jpg?1783938350",
    releaseDate = "2015-07-17",
    rarity = Rarity.COMMON,
)
