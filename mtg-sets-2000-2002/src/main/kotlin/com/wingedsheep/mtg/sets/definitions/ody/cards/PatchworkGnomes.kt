package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Patchwork Gnomes reprint in ODY.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * TMP's `cards/` package (the card's earliest real printing). This file contributes only
 * the ODY-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PatchworkGnomesReprint = Printing(
    oracleId = "00ad27a1-9162-408d-ac75-970e45d7e06c",
    name = "Patchwork Gnomes",
    setCode = "ODY",
    collectorNumber = "306",
    artist = "Jerry Tiritilli",
    imageUri = "https://cards.scryfall.io/normal/front/c/d/cd8958d5-e4a9-42ee-ae82-d184c4b92c9d.jpg",
    releaseDate = "2001-10-01",
    rarity = Rarity.UNCOMMON,
)
