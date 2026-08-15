package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Blossoming Sands reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BlossomingSandsReprint = Printing(
    oracleId = "45429b2c-be3b-4b2e-9bab-a059ccbda8cd",
    name = "Blossoming Sands",
    setCode = "TDM",
    collectorNumber = "251",
    scryfallId = "0a9df994-e0f4-4919-af99-4f643eb9199c",
    artist = "Piotr Dura",
    imageUri = "https://cards.scryfall.io/normal/front/0/a/0a9df994-e0f4-4919-af99-4f643eb9199c.jpg?1743204991",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
