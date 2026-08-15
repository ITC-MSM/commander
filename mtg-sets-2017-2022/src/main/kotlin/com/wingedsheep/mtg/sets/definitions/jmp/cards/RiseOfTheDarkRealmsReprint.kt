package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rise of the Dark Realms reprint in Jumpstart (JMP).
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (spell script) lives in Magic 2014's
 * `cards/` package. This file contributes only the JMP-specific presentation row — set, collector
 * number, art — picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the
 * set's `printings`.
 */
val RiseOfTheDarkRealmsReprint = Printing(
    oracleId = "e5223a09-f732-4747-8914-e6546ab0ef4c",
    name = "Rise of the Dark Realms",
    setCode = "JMP",
    collectorNumber = "271",
    scryfallId = "7c0e1064-47c3-4f03-a1f2-3bcb356db82b",
    artist = "Michael Komarck",
    imageUri = "https://cards.scryfall.io/normal/front/7/c/7c0e1064-47c3-4f03-a1f2-3bcb356db82b.jpg?1783930410",
    releaseDate = "2020-07-17",
    rarity = Rarity.MYTHIC,
)
