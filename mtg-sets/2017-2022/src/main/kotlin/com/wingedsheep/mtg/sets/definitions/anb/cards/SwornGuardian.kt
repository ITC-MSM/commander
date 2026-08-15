package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sworn Guardian reprint in ANB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file
 * contributes only the ANB-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SwornGuardianReprint = Printing(
    oracleId = "9f63ab29-b86e-4337-b9dd-2b35c8415f90",
    name = "Sworn Guardian",
    setCode = "ANB",
    collectorNumber = "35",
    artist = "Sara Winters",
    imageUri = "https://cards.scryfall.io/normal/front/a/3/a32a86fb-3652-4ac7-a879-c78899c493d6.jpg?1783929829",
    releaseDate = "2020-08-13",
    rarity = Rarity.COMMON,
)
