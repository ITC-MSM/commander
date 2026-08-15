package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Filigree Familiar reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * KLD's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FiligreeFamiliarReprint = Printing(
    oracleId = "b544f690-e4bf-4a5b-984d-9256518fd574",
    name = "Filigree Familiar",
    setCode = "CMR",
    collectorNumber = "308",
    artist = "Izzy",
    imageUri = "https://cards.scryfall.io/normal/front/8/7/875df3ef-fab4-455f-bfdb-8f6361b27bf6.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
