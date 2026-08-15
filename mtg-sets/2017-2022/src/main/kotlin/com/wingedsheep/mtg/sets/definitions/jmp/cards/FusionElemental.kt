package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fusion Elemental reprint in JMP.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * CON's `cards/` package (the card's earliest real printing). This file
 * contributes only the JMP-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FusionElementalReprint = Printing(
    oracleId = "a152674f-be29-40ab-8dd9-376fd4eb3bb8",
    name = "Fusion Elemental",
    setCode = "JMP",
    collectorNumber = "451",
    artist = "Michael Komarck",
    imageUri = "https://cards.scryfall.io/normal/front/5/5/5538bc51-e320-437e-867d-0d01621e31fb.jpg?1783930345",
    releaseDate = "2020-07-17",
    rarity = Rarity.UNCOMMON,
)
