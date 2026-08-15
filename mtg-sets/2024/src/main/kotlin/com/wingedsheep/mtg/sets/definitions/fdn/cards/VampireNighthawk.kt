package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vampire Nighthawk reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ZEN's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VampireNighthawkReprint = Printing(
    oracleId = "feb244f8-bcb1-44cf-9940-2719221a7309",
    name = "Vampire Nighthawk",
    setCode = "FDN",
    collectorNumber = "757",
    artist = "Jason Chan",
    imageUri = "https://cards.scryfall.io/normal/front/7/a/7aff07f9-9528-4149-9af0-f4e3c66c9dc5.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
