package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wind-Scarred Crag reprint in DFT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DFT-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WindScarredCragReprint = Printing(
    oracleId = "b0af0c54-2a59-4075-8543-d41ff20c4c87",
    name = "Wind-Scarred Crag",
    setCode = "DFT",
    collectorNumber = "271",
    scryfallId = "882969d8-7e3e-4f32-858a-00da20c67b83",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/8/8/882969d8-7e3e-4f32-858a-00da20c67b83.jpg?1738356981",
    releaseDate = "2025-02-14",
    rarity = Rarity.COMMON,
)
