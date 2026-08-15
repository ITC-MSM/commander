package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Starstorm reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StarstormReprint = Printing(
    oracleId = "34d112c3-cc2d-4428-bd7f-568bda61bd30",
    name = "Starstorm",
    setCode = "BLC",
    collectorNumber = "203",
    scryfallId = "a5f3ab82-2a24-4c6c-bdd9-f2bd5adca144",
    artist = "Jonas De Ro",
    imageUri = "https://cards.scryfall.io/normal/front/a/5/a5f3ab82-2a24-4c6c-bdd9-f2bd5adca144.jpg?1721429191",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
