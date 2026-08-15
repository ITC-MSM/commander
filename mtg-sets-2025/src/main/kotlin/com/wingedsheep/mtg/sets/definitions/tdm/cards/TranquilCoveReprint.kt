package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tranquil Cove reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TranquilCoveReprint = Printing(
    oracleId = "5d641bf6-0f93-4189-8dc1-ec7ea446dade",
    name = "Tranquil Cove",
    setCode = "TDM",
    collectorNumber = "270",
    scryfallId = "1c4efa6c-4f29-41cd-a728-bf0e479ace05",
    artist = "Kevin Sidharta",
    imageUri = "https://cards.scryfall.io/normal/front/1/c/1c4efa6c-4f29-41cd-a728-bf0e479ace05.jpg?1743205066",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
