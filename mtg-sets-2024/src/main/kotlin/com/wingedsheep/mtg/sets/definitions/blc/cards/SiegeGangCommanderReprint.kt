package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Siege-Gang Commander reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SiegeGangCommanderReprint = Printing(
    oracleId = "ddc7f59a-bbb1-4ba1-82c8-6813fd191940",
    name = "Siege-Gang Commander",
    setCode = "BLC",
    collectorNumber = "202",
    scryfallId = "0befe0f5-ff7c-441a-9bb2-cbe919be10ad",
    artist = "Aaron Miller",
    imageUri = "https://cards.scryfall.io/normal/front/0/b/0befe0f5-ff7c-441a-9bb2-cbe919be10ad.jpg?1721429184",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
