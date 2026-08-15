package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Angelic Wall reprint in ODY.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * P02's `cards/` package (the card's earliest real printing). This file contributes only
 * the ODY-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AngelicWallReprint = Printing(
    oracleId = "4502b24f-604b-4e36-9168-31c1a1ab4dab",
    name = "Angelic Wall",
    setCode = "ODY",
    collectorNumber = "3",
    artist = "John Avon",
    imageUri = "https://cards.scryfall.io/normal/front/6/d/6d7c9675-e663-4ad1-9271-38d5c050a7c7.jpg",
    releaseDate = "2001-10-01",
    rarity = Rarity.COMMON,
)
