package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jaya's Immolating Inferno reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DOM's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val JayasImmolatingInfernoReprint = Printing(
    oracleId = "f5f0deb0-070a-45b6-9b81-0bc8143f3040",
    name = "Jaya's Immolating Inferno",
    setCode = "CMR",
    collectorNumber = "415",
    artist = "Noah Bradley",
    imageUri = "https://cards.scryfall.io/normal/front/3/6/365336b4-92ee-429d-8f18-4624cba5469d.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
)
