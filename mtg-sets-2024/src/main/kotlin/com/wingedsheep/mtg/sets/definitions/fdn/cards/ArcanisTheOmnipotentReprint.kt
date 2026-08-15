package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Arcanis the Omnipotent reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ArcanisTheOmnipotentReprint = Printing(
    oracleId = "8a7183cc-161c-444d-a889-a17519c8061b",
    name = "Arcanis the Omnipotent",
    setCode = "FDN",
    collectorNumber = "741",
    scryfallId = "9d31e3e3-f398-4c0e-a2c9-4c614a4d41de",
    artist = "Justin Sweet",
    imageUri = "https://cards.scryfall.io/normal/front/9/d/9d31e3e3-f398-4c0e-a2c9-4c614a4d41de.jpg?1775599764",
    releaseDate = "2026-04-24",
    rarity = Rarity.RARE,
)
