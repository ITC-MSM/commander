package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wishclaw Talisman reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in ELD's `cards/`
 * package (the card's earliest real printing). This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WishclawTalismanReprint = Printing(
    oracleId = "81c70ae7-3c18-4c9b-8505-e4db9e0e6518",
    name = "Wishclaw Talisman",
    setCode = "FDN",
    collectorNumber = "617",
    scryfallId = "69d0f5bd-ccea-49b2-bd79-ad5e4d850cf5",
    artist = "Daarken",
    imageUri = "https://cards.scryfall.io/normal/front/6/9/69d0f5bd-ccea-49b2-bd79-ad5e4d850cf5.jpg?1783908926",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
