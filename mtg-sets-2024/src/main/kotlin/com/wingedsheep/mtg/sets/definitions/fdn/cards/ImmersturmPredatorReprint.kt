package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Immersturm Predator reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * Kaldheim's `cards/` package. This file contributes only the FDN-specific presentation
 * row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ImmersturmPredatorReprint = Printing(
    oracleId = "fcb6c6d4-ea8d-49cf-b6df-377084bdb757",
    name = "Immersturm Predator",
    setCode = "FDN",
    collectorNumber = "660",
    scryfallId = "52dfc234-e87a-4594-8dcf-0a2f95eedc97",
    artist = "Nicholas Gregory",
    imageUri = "https://cards.scryfall.io/normal/front/5/2/52dfc234-e87a-4594-8dcf-0a2f95eedc97.jpg?1783908910",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
