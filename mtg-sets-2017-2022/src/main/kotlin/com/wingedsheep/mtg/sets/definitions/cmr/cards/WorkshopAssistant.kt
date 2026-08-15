package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Workshop Assistant reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * KLD's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WorkshopAssistantReprint = Printing(
    oracleId = "319ee221-bace-41e9-b386-6b528bd780c1",
    name = "Workshop Assistant",
    setCode = "CMR",
    collectorNumber = "348",
    artist = "Victor Adame Minguez",
    imageUri = "https://cards.scryfall.io/normal/front/e/9/e9387e8f-0e72-4212-81c8-e64050700c52.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
