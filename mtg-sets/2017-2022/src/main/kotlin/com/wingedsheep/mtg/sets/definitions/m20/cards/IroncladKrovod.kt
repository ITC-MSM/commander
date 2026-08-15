package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ironclad Krovod reprint in M20.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * WAR's `cards/` package (the card's earliest real printing). This file
 * contributes only the M20-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val IroncladKrovodReprint = Printing(
    oracleId = "bd1c9b5f-3b70-457c-8dd8-2420ce1c0d7c",
    name = "Ironclad Krovod",
    setCode = "M20",
    collectorNumber = "308",
    artist = "Sam Rowan",
    imageUri = "https://cards.scryfall.io/normal/front/e/5/e5d57e98-e05a-4a89-900e-20fe675a62ef.jpg?1783932912",
    releaseDate = "2019-07-12",
    rarity = Rarity.COMMON,
)
