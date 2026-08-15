package com.wingedsheep.mtg.sets.definitions.jud.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Guided Strike reprint in JUD.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * WTH's `cards/` package (the card's earliest real printing). This file contributes only
 * the JUD-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GuidedStrikeReprint = Printing(
    oracleId = "a215e813-df7e-4f21-9718-eb264eb62813",
    name = "Guided Strike",
    setCode = "JUD",
    collectorNumber = "13",
    artist = "Dave Dorman",
    imageUri = "https://cards.scryfall.io/normal/front/d/1/d13138c1-e98f-4803-8c68-ffc80139c168.jpg",
    releaseDate = "2002-05-27",
    rarity = Rarity.COMMON,
)
