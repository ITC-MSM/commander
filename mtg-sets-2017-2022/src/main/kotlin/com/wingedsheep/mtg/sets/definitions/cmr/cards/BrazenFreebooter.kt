package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Brazen Freebooter reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BrazenFreebooterReprint = Printing(
    oracleId = "13e8d063-eece-4f38-83f6-02403130defa",
    name = "Brazen Freebooter",
    setCode = "CMR",
    collectorNumber = "164",
    artist = "Randy Gallegos",
    imageUri = "https://cards.scryfall.io/normal/front/4/5/45657345-b564-4e5f-a57a-01dc54df7e7c.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
