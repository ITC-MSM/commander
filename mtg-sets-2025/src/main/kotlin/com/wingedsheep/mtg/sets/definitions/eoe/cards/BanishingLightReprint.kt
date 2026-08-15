package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Banishing Light reprint in Edge of Eternities.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * `mtg-sets/.../definitions/blb/cards/BanishingLight.kt`. This file contributes only the
 * EOE-specific presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via `EdgeOfEternitiesSet.printings`.
 */
val BanishingLightReprint = Printing(
    oracleId = "f28b21a6-f7ce-437a-8c5b-0423cb55cefb",
    name = "Banishing Light",
    setCode = "EOE",
    collectorNumber = "6",
    scryfallId = "c45f11cd-a0aa-4d14-aa21-57f0969f3e2b",
    artist = "Rovina Cai",
    imageUri = "https://cards.scryfall.io/normal/front/c/4/c45f11cd-a0aa-4d14-aa21-57f0969f3e2b.jpg?1752946576",
    releaseDate = "2025-08-01",
    rarity = Rarity.COMMON,
)
