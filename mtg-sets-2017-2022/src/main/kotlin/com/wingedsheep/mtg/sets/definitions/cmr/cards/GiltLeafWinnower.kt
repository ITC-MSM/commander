package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gilt-Leaf Winnower reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ORI's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GiltLeafWinnowerReprint = Printing(
    oracleId = "53b99846-3f80-4e9d-ad3c-52a4906b8823",
    name = "Gilt-Leaf Winnower",
    setCode = "CMR",
    collectorNumber = "130",
    artist = "Viktor Titov",
    imageUri = "https://cards.scryfall.io/normal/front/f/e/fea7fbfc-c17d-4a5b-863e-842d4040200a.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
