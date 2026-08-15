package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Intangible Virtue reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ISD's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val IntangibleVirtueReprint = Printing(
    oracleId = "d21c3c8f-d105-4ba9-bf69-e5f26f0f8ec5",
    name = "Intangible Virtue",
    setCode = "CMR",
    collectorNumber = "24",
    artist = "Clint Cearley",
    imageUri = "https://cards.scryfall.io/normal/front/5/3/535764b9-67b2-4123-a4be-2aa72fcd8a33.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
