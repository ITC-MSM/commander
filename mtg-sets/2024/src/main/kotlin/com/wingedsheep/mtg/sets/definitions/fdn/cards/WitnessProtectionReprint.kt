package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Witness Protection reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * Streets of New Capenna's `cards/` package — see
 * `com.wingedsheep.mtg.sets.definitions.snc.cards.WitnessProtection`. This file contributes
 * only the FDN-specific presentation row — set, collector number, art — picked up
 * automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WitnessProtectionReprint = Printing(
    oracleId = "a5f5e71e-83a7-468d-829a-6392b764c76e",
    name = "Witness Protection",
    setCode = "FDN",
    collectorNumber = "168",
    scryfallId = "f231e981-0069-43ce-ac1c-c85ced613e93",
    artist = "Dominik Mayer",
    imageUri = "https://cards.scryfall.io/normal/front/f/2/f231e981-0069-43ce-ac1c-c85ced613e93.jpg?1782689122",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
