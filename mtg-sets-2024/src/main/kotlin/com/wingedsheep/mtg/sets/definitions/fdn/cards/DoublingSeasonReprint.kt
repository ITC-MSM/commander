package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Doubling Season reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Ravnica: City of Guilds'
 * `cards/` package (its earliest real printing). This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DoublingSeasonReprint = Printing(
    oracleId = "01546b7d-a233-4176-8843-d732074dc5b6",
    name = "Doubling Season",
    setCode = "FDN",
    collectorNumber = "216",
    scryfallId = "f2c4f80e-84a0-463b-82c3-5c6503809351",
    artist = "Chuck Lukacs",
    imageUri = "https://cards.scryfall.io/normal/front/f/2/f2c4f80e-84a0-463b-82c3-5c6503809351.jpg?1783909062",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)
