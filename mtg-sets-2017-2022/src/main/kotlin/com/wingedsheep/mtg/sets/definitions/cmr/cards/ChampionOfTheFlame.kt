package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Champion of the Flame reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DOM's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ChampionOfTheFlameReprint = Printing(
    oracleId = "d9dbe40f-38e9-460a-b84f-4aa22650017e",
    name = "Champion of the Flame",
    setCode = "CMR",
    collectorNumber = "167",
    artist = "Wayne Reynolds",
    imageUri = "https://cards.scryfall.io/normal/front/a/2/a248363d-40a0-4dc5-a0bb-a826c3ba4499.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
