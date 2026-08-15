package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Blossoming Sands reprint in BLB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLB-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BlossomingSandsReprint = Printing(
    oracleId = "45429b2c-be3b-4b2e-9bab-a059ccbda8cd",
    name = "Blossoming Sands",
    setCode = "BLB",
    collectorNumber = "396",
    scryfallId = "ecfa2cc9-e427-4104-bad7-b0294e392b1f",
    artist = "Sam Burley",
    imageUri = "https://cards.scryfall.io/normal/front/e/c/ecfa2cc9-e427-4104-bad7-b0294e392b1f.jpg?1721428122",
    releaseDate = "2024-08-02",
    rarity = Rarity.COMMON,
)
