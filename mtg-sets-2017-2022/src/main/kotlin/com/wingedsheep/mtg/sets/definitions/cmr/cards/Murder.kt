package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Murder reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M13's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MurderReprint = Printing(
    oracleId = "938b4e2c-88d9-4637-bc00-e228920c9a78",
    name = "Murder",
    setCode = "CMR",
    collectorNumber = "134",
    artist = "Allen Williams",
    imageUri = "https://cards.scryfall.io/normal/front/4/4/440bfb8c-f29a-4c11-9fcb-ee935dead03f.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
