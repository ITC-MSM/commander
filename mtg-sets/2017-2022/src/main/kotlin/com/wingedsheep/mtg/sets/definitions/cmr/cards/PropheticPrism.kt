package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Prophetic Prism reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ROE's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PropheticPrismReprint = Printing(
    oracleId = "134a9877-5cfb-4a2a-a0f0-930dce45f58b",
    name = "Prophetic Prism",
    setCode = "CMR",
    collectorNumber = "334",
    artist = "John Avon",
    imageUri = "https://cards.scryfall.io/normal/front/1/4/14602fed-8666-4884-8fca-13529578f9e2.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
