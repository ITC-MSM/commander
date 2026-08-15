package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Disdainful Stroke reprint in WOE.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the WOE-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DisdainfulStrokeReprint = Printing(
    oracleId = "11e02134-7b1a-46a4-a89e-7539dd1efada",
    name = "Disdainful Stroke",
    setCode = "WOE",
    collectorNumber = "47",
    scryfallId = "588c6217-c460-417e-98bf-de5475780baf",
    artist = "Eelis Kyttanen",
    imageUri = "https://cards.scryfall.io/normal/front/5/8/588c6217-c460-417e-98bf-de5475780baf.jpg?1693867173",
    releaseDate = "2023-09-08",
    rarity = Rarity.UNCOMMON,
)
