package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Farhaven Elf reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * SHM's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FarhavenElfReprint = Printing(
    oracleId = "4ce2357f-93e6-40ca-beca-8f4e15adc464",
    name = "Farhaven Elf",
    setCode = "CMR",
    collectorNumber = "225",
    artist = "Brandon Kitkouski",
    imageUri = "https://cards.scryfall.io/normal/front/d/b/db681025-f912-4cba-8b89-c57cda2e6d53.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
