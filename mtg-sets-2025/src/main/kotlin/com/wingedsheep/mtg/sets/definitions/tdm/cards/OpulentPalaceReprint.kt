package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Opulent Palace reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OpulentPalaceReprint = Printing(
    oracleId = "f9e7e855-1e3b-42d3-91b0-64ba8b5b8982",
    name = "Opulent Palace",
    setCode = "TDM",
    collectorNumber = "264",
    scryfallId = "21cb3b3b-0738-4c2e-a3fc-927fd6b9d3fb",
    artist = "Sergey Glushakov",
    imageUri = "https://cards.scryfall.io/normal/front/2/1/21cb3b3b-0738-4c2e-a3fc-927fd6b9d3fb.jpg?1743205041",
    releaseDate = "2025-04-11",
    rarity = Rarity.UNCOMMON,
)
