package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Stroke of Midnight reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * WOE's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StrokeOfMidnightReprint = Printing(
    oracleId = "9a107e48-3d50-4941-95b1-10f2b29a4245",
    name = "Stroke of Midnight",
    setCode = "FDN",
    collectorNumber = "148",
    artist = "Julia Metzger",
    imageUri = "https://cards.scryfall.io/normal/front/a/b/ab135925-d924-456d-851a-6ccdaaf27271.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
