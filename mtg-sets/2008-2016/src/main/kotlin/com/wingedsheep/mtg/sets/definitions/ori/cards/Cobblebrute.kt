package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cobblebrute reprint in ORI.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * RTR's `cards/` package (the card's earliest real printing). This file
 * contributes only the ORI-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CobblebruteReprint = Printing(
    oracleId = "6d4ed1f2-0918-444c-95ef-d58beb5aa216",
    name = "Cobblebrute",
    setCode = "ORI",
    collectorNumber = "138",
    artist = "Eytan Zana",
    imageUri = "https://cards.scryfall.io/normal/front/f/f/ffa87a70-c9fb-4ab3-ac16-367888aa775b.jpg?1783938331",
    releaseDate = "2015-07-17",
    rarity = Rarity.COMMON,
)
