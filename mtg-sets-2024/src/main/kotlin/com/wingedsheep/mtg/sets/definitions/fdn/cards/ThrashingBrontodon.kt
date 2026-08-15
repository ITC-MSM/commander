package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Thrashing Brontodon reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ThrashingBrontodonReprint = Printing(
    oracleId = "60bc63dc-ac9f-4a2f-aef5-c90d0aa31553",
    name = "Thrashing Brontodon",
    setCode = "FDN",
    collectorNumber = "560",
    artist = "Jakub Kasper",
    imageUri = "https://cards.scryfall.io/normal/front/5/9/592364bf-68b5-4ea0-97d6-c7cadce940fd.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
