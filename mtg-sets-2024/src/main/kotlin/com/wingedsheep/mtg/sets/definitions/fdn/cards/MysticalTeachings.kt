package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mystical Teachings reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * TSP's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MysticalTeachingsReprint = Printing(
    oracleId = "81d72511-88b4-45ec-a5a9-e722caf55577",
    name = "Mystical Teachings",
    setCode = "FDN",
    collectorNumber = "594",
    artist = "Ron Spears",
    imageUri = "https://cards.scryfall.io/normal/front/3/f/3f88eacf-5c7b-4a35-86f0-af3b0516d4c2.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
