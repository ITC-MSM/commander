package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dreamstone Hedron reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ROE's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DreamstoneHedronReprint = Printing(
    oracleId = "0e2575be-c596-4c8c-bf07-7941ca065721",
    name = "Dreamstone Hedron",
    setCode = "CMR",
    collectorNumber = "307",
    artist = "Eric Deschamps",
    imageUri = "https://cards.scryfall.io/normal/front/7/7/77a0b45f-d044-4d59-81fd-c2683beca401.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
