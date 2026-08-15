package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Elspeth's Smite reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MOM's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ElspethsSmiteReprint = Printing(
    oracleId = "3f404fe4-4335-4dcc-ba90-78246c4b880b",
    name = "Elspeth's Smite",
    setCode = "FDN",
    collectorNumber = "493",
    artist = "Livia Prima",
    imageUri = "https://cards.scryfall.io/normal/front/b/4/b405a442-cf6f-4c0b-9950-f208b30c052e.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
