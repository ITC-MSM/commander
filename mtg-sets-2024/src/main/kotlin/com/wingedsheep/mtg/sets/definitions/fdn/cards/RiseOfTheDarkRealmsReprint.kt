package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rise of the Dark Realms reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (spell script) lives in Magic 2014's
 * `cards/` package. This file contributes only the FDN-specific presentation row — set,
 * collector number, art — picked up automatically by `CardDiscovery.findPrintingsIn` and
 * surfaced via the set's `printings`.
 */
val RiseOfTheDarkRealmsReprint = Printing(
    oracleId = "e5223a09-f732-4747-8914-e6546ab0ef4c",
    name = "Rise of the Dark Realms",
    setCode = "FDN",
    collectorNumber = "183",
    scryfallId = "8645bf0c-631f-4003-bd24-3e069ae23513",
    artist = "Michael Komarck",
    imageUri = "https://cards.scryfall.io/normal/front/8/6/8645bf0c-631f-4003-bd24-3e069ae23513.jpg?1783909071",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)
