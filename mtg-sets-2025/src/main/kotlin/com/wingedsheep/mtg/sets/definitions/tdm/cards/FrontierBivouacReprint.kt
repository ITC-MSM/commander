package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Frontier Bivouac reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FrontierBivouacReprint = Printing(
    oracleId = "e4cf6c2f-0f1e-4980-9ef9-e4eabcae42a9",
    name = "Frontier Bivouac",
    setCode = "TDM",
    collectorNumber = "256",
    scryfallId = "679fff07-4796-4d91-8dd6-4e294383ce88",
    artist = "Andrea Piparo",
    imageUri = "https://cards.scryfall.io/normal/front/6/7/679fff07-4796-4d91-8dd6-4e294383ce88.jpg?1743205011",
    releaseDate = "2025-04-11",
    rarity = Rarity.UNCOMMON,
)
