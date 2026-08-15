package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gallantry reprint in ODY.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * TMP's `cards/` package (the card's earliest real printing). This file contributes only
 * the ODY-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GallantryReprint = Printing(
    oracleId = "e8cd7d7c-71b7-4924-bf9c-5e6fd91447f0",
    name = "Gallantry",
    setCode = "ODY",
    collectorNumber = "23",
    artist = "Mark Tedin",
    imageUri = "https://cards.scryfall.io/normal/front/d/7/d7992f83-93ad-4132-b8f2-a9f93bc96b4e.jpg",
    releaseDate = "2001-10-01",
    rarity = Rarity.UNCOMMON,
)
