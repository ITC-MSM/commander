package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dragonmaster Outcast reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * WWK's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DragonmasterOutcastReprint = Printing(
    oracleId = "b6fb79c3-cd32-4045-8177-e52841eea65b",
    name = "Dragonmaster Outcast",
    setCode = "FDN",
    collectorNumber = "622",
    artist = "Raymond Swanland",
    imageUri = "https://cards.scryfall.io/normal/front/b/7/b720f3a8-f38f-4460-86a3-dad541e7add7.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)
