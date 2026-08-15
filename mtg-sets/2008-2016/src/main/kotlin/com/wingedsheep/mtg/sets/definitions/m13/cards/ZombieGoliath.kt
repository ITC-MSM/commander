package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Zombie Goliath reprint in M13.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M13-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ZombieGoliathReprint = Printing(
    oracleId = "eadd88b6-e75a-4482-8382-561718121772",
    name = "Zombie Goliath",
    setCode = "M13",
    collectorNumber = "119",
    artist = "E. M. Gist",
    imageUri = "https://cards.scryfall.io/normal/front/8/6/8638edec-ddcd-4f50-9c2f-2e1668e3d175.jpg?1783940490",
    releaseDate = "2012-07-13",
    rarity = Rarity.COMMON,
)
