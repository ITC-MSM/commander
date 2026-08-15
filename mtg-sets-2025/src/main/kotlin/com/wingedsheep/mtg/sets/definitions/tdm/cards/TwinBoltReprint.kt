package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Twin Bolt reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (spell script) lives in the DTK
 * `cards/` package — DTK is the card's earliest real-expansion printing. This file
 * contributes only the TDM-specific presentation row, picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TwinBoltReprint = Printing(
    oracleId = "970dc070-3668-4584-9904-2897b27bb806",
    name = "Twin Bolt",
    setCode = "TDM",
    collectorNumber = "128",
    scryfallId = "688d8e93-d071-4089-9ef9-565ac4ae9ae0",
    artist = "Craig J Spearing",
    imageUri = "https://cards.scryfall.io/normal/front/6/8/688d8e93-d071-4089-9ef9-565ac4ae9ae0.jpg?1743204476",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
