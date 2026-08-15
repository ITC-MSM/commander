package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ravenous Giant reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MH1's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RavenousGiantReprint = Printing(
    oracleId = "46c7a552-4a98-4e1a-8651-8fb0e7153aec",
    name = "Ravenous Giant",
    setCode = "FDN",
    collectorNumber = "630",
    artist = "Milivoj Ćeran",
    imageUri = "https://cards.scryfall.io/normal/front/0/5/050b70c4-9086-4db4-8e65-f9bb5f67cf1b.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
