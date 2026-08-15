package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Thornwood Falls reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ThornwoodFallsReprint = Printing(
    oracleId = "ec96cde2-f1e6-495c-94e2-3e8ae79e556c",
    name = "Thornwood Falls",
    setCode = "MOM",
    collectorNumber = "274",
    scryfallId = "f1b753e2-6e53-4ed1-9be4-66f8eb005a11",
    artist = "Roman Kuteynikov",
    imageUri = "https://cards.scryfall.io/normal/front/f/1/f1b753e2-6e53-4ed1-9be4-66f8eb005a11.jpg?1682205902",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
