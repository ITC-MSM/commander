package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wind-Scarred Crag reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WindScarredCragReprint = Printing(
    oracleId = "b0af0c54-2a59-4075-8543-d41ff20c4c87",
    name = "Wind-Scarred Crag",
    setCode = "MOM",
    collectorNumber = "276",
    scryfallId = "7f2642cd-e3cc-4aab-8c00-4987284509b3",
    artist = "Roman Kuteynikov",
    imageUri = "https://cards.scryfall.io/normal/front/7/f/7f2642cd-e3cc-4aab-8c00-4987284509b3.jpg?1682205924",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
