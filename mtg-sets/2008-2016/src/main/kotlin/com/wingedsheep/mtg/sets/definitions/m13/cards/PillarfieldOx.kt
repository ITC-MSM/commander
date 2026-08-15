package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Pillarfield Ox reprint in M13.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * ZEN's `cards/` package (the card's earliest real printing). This file
 * contributes only the M13-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PillarfieldOxReprint = Printing(
    oracleId = "0f7c70c4-9795-4c76-8b27-043942a963c6",
    name = "Pillarfield Ox",
    setCode = "M13",
    collectorNumber = "25",
    artist = "Andrew Robinson",
    imageUri = "https://cards.scryfall.io/normal/front/3/3/33e2f3ae-bf92-478b-9c63-acc3f175f02a.jpg?1783940516",
    releaseDate = "2012-07-13",
    rarity = Rarity.COMMON,
)
