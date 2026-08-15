package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Elvish Archdruid reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ElvishArchdruidReprint = Printing(
    oracleId = "6e2c2423-d854-4478-99e6-64f29851f026",
    name = "Elvish Archdruid",
    setCode = "FDN",
    collectorNumber = "219",
    artist = "Karl Kopinski",
    imageUri = "https://cards.scryfall.io/normal/front/3/4/341da856-7414-403b-b2e3-4bebd58a5aa4.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
