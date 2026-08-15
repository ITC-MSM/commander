package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Firebomb reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * BRO's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GoblinFirebombReprint = Printing(
    oracleId = "2625fa9c-638c-46d3-966b-1166f440186f",
    name = "Goblin Firebomb",
    setCode = "FDN",
    collectorNumber = "562",
    artist = "Noah Thatcher",
    imageUri = "https://cards.scryfall.io/normal/front/5/5/55ab3f99-9541-4100-818c-5d9e916791e4.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
