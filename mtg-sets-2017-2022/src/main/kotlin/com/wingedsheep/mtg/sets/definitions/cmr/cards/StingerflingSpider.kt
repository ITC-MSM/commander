package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Stingerfling Spider reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M12's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StingerflingSpiderReprint = Printing(
    oracleId = "056770e2-8ab7-424d-a2d8-7be6d6cc73f1",
    name = "Stingerfling Spider",
    setCode = "CMR",
    collectorNumber = "258",
    artist = "Dave Allsop",
    imageUri = "https://cards.scryfall.io/normal/front/1/8/18f45424-2c26-43fa-9afe-eeb22ce772e1.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
