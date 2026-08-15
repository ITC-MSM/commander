package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Banishing Light reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BanishingLightReprint = Printing(
    oracleId = "f28b21a6-f7ce-437a-8c5b-0423cb55cefb",
    name = "Banishing Light",
    setCode = "FDN",
    collectorNumber = "138",
    scryfallId = "e38dc3b3-1629-491b-8afd-0e7a9a857713",
    artist = "Willian Murai",
    imageUri = "https://cards.scryfall.io/normal/front/e/3/e38dc3b3-1629-491b-8afd-0e7a9a857713.jpg?1732131144",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
