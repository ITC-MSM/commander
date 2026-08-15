package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Demonic Pact reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in ORI's `cards/`
 * package (the card's earliest real printing). This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DemonicPactReprint = Printing(
    oracleId = "19a2f0a0-9e68-4982-a5f5-b77d805befd7",
    name = "Demonic Pact",
    setCode = "FDN",
    collectorNumber = "602",
    scryfallId = "5b0b7242-df91-48cf-bf4e-b68306c9965f",
    artist = "Manuel Castañón",
    imageUri = "https://cards.scryfall.io/normal/front/5/b/5b0b7242-df91-48cf-bf4e-b68306c9965f.jpg?1783908933",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)
