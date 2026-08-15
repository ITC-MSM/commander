package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rugged Highlands reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RuggedHighlandsReprint = Printing(
    oracleId = "6c922206-6e68-4dcd-9559-88da1074f2c4",
    name = "Rugged Highlands",
    setCode = "TDM",
    collectorNumber = "265",
    scryfallId = "31261eca-28ad-407c-84ef-0c124d0d7451",
    artist = "Carlos Palma Cruchaga",
    imageUri = "https://cards.scryfall.io/normal/front/3/1/31261eca-28ad-407c-84ef-0c124d0d7451.jpg?1743205049",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
