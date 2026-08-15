package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Herald of Faith reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HeraldOfFaithReprint = Printing(
    oracleId = "cb97da84-1d13-4795-b68a-2bf111a50067",
    name = "Herald of Faith",
    setCode = "FDN",
    collectorNumber = "735",
    artist = "Tommy Arnold",
    imageUri = "https://cards.scryfall.io/normal/front/2/e/2e1705da-dc35-4bcb-82d4-b77712e79af3.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
