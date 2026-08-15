package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Spellgorger Weird reprint in Core Set 2021.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * War of the Spark's `cards/` package. This file contributes only the M21 presentation row.
 */
val SpellgorgerWeirdReprint = Printing(
    oracleId = "550811ca-0995-4886-aa8c-5cee118aa54f",
    name = "Spellgorger Weird",
    setCode = "M21",
    collectorNumber = "161",
    scryfallId = "baefa06e-28ba-41b9-866e-1ed0c4969852",
    artist = "James Paick",
    imageUri = "https://cards.scryfall.io/normal/front/b/a/baefa06e-28ba-41b9-866e-1ed0c4969852.jpg?1594736783",
    releaseDate = "2020-07-03",
    rarity = Rarity.COMMON,
)
