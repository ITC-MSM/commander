package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mystic Monastery reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MysticMonasteryReprint = Printing(
    oracleId = "834b8f71-9a45-42ae-9e99-e749fa6fb45e",
    name = "Mystic Monastery",
    setCode = "BLC",
    collectorNumber = "318",
    scryfallId = "c59b9295-1802-4452-ae8b-72adeb566c3a",
    artist = "Florian de Gesincourt",
    imageUri = "https://cards.scryfall.io/normal/front/c/5/c59b9295-1802-4452-ae8b-72adeb566c3a.jpg?1721429785",
    releaseDate = "2024-08-02",
    rarity = Rarity.UNCOMMON,
)
