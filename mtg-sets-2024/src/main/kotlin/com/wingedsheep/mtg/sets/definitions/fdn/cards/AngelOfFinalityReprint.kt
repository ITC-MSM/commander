package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Angel of Finality reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Commander 2013's `cards/`
 * package (its earliest real printing). This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AngelOfFinalityReprint = Printing(
    oracleId = "48d14b5c-711a-4f8a-9de5-55415cb7a79a",
    name = "Angel of Finality",
    setCode = "FDN",
    collectorNumber = "136",
    scryfallId = "baaabd52-3aa9-4e2f-9369-d4db8b405ba8",
    artist = "Howard Lyon",
    imageUri = "https://cards.scryfall.io/normal/front/b/a/baaabd52-3aa9-4e2f-9369-d4db8b405ba8.jpg?1783909087",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
