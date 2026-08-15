package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Exclusion Mage reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ExclusionMageReprint = Printing(
    oracleId = "fd28fe55-36cd-4242-8dd4-edb58fbb9895",
    name = "Exclusion Mage",
    setCode = "FDN",
    collectorNumber = "508",
    artist = "Chris Seaman",
    imageUri = "https://cards.scryfall.io/normal/front/6/2/62c8024e-490a-484a-bcee-da7728a64a1f.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
