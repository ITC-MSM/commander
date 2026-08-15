package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mystic Monastery reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MysticMonasteryReprint = Printing(
    oracleId = "834b8f71-9a45-42ae-9e99-e749fa6fb45e",
    name = "Mystic Monastery",
    setCode = "TDM",
    collectorNumber = "262",
    scryfallId = "c7b8a01c-c400-47c7-8270-78902efe850e",
    artist = "Leon Tukker",
    imageUri = "https://cards.scryfall.io/normal/front/c/7/c7b8a01c-c400-47c7-8270-78902efe850e.jpg?1743205034",
    releaseDate = "2025-04-11",
    rarity = Rarity.UNCOMMON,
)
