package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Obliterating Bolt reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * BRO's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ObliteratingBoltReprint = Printing(
    oracleId = "57fe941c-a830-4570-afe2-18f93c7a7b84",
    name = "Obliterating Bolt",
    setCode = "FDN",
    collectorNumber = "629",
    artist = "Campbell White",
    imageUri = "https://cards.scryfall.io/normal/front/d/5/d59e836b-1c7b-43ac-889b-5f94e1638c42.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
