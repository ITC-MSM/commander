package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Zombie Goliath reprint in M12.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M12-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ZombieGoliathReprint = Printing(
    oracleId = "eadd88b6-e75a-4482-8382-561718121772",
    name = "Zombie Goliath",
    setCode = "M12",
    collectorNumber = "119",
    artist = "E. M. Gist",
    imageUri = "https://cards.scryfall.io/normal/front/1/9/1985e0bd-05b9-4eaf-9333-6262cf677acd.jpg?1783941077",
    releaseDate = "2011-07-15",
    rarity = Rarity.COMMON,
)
