package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Memory Deluge reprint in MID.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MID-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MemoryDelugeReprint = Printing(
    oracleId = "e6fd55f2-7e26-469c-a44a-ea2eb90e19a9",
    name = "Memory Deluge",
    setCode = "MID",
    collectorNumber = "62",
    scryfallId = "dc00fd1b-3dd9-492a-9ed4-0b6743074730",
    artist = "Lake Hurwitz",
    imageUri = "https://cards.scryfall.io/normal/front/d/c/dc00fd1b-3dd9-492a-9ed4-0b6743074730.jpg?1634349038",
    releaseDate = "2021-09-24",
    rarity = Rarity.RARE,
)
