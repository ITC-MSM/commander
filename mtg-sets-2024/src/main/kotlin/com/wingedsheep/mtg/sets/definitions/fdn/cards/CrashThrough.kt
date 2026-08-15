package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Crash Through reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * HOU's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CrashThroughReprint = Printing(
    oracleId = "41c15160-ee8c-42e3-bc6c-593b8c8ae335",
    name = "Crash Through",
    setCode = "FDN",
    collectorNumber = "620",
    artist = "Izzy",
    imageUri = "https://cards.scryfall.io/normal/front/9/e/9eb16918-6363-4849-8d74-e26822a0ddf7.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
