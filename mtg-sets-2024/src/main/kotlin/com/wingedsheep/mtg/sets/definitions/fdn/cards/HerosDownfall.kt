package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Hero's Downfall reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * THS's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HerosDownfallReprint = Printing(
    oracleId = "03df6a57-37c9-46d3-83b3-4a6240100714",
    name = "Hero's Downfall",
    setCode = "FDN",
    collectorNumber = "175",
    artist = "Chris Rallis",
    imageUri = "https://cards.scryfall.io/normal/front/a/d/ad2c01d9-8f54-46c0-9dc9-d4d4764ce1c9.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
