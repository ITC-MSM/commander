package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Blossoming Sands reprint in DFT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DFT-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BlossomingSandsReprint = Printing(
    oracleId = "45429b2c-be3b-4b2e-9bab-a059ccbda8cd",
    name = "Blossoming Sands",
    setCode = "DFT",
    collectorNumber = "252",
    scryfallId = "69949863-2510-4fe2-a815-0682beeb08a3",
    artist = "Valera Lutfullina",
    imageUri = "https://cards.scryfall.io/normal/front/6/9/69949863-2510-4fe2-a815-0682beeb08a3.jpg?1738356887",
    releaseDate = "2025-02-14",
    rarity = Rarity.COMMON,
)
