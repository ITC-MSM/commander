package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dark Confidant reprint in FIN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * the Ravnica: City of Guilds (`rav`) `cards/` package — the earliest real printing. This
 * file contributes only the FIN-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's
 * `printings`.
 */
val DarkConfidantReprint = Printing(
    oracleId = "2068185c-1b50-47d0-aa3f-bf505d199428",
    name = "Dark Confidant",
    setCode = "FIN",
    collectorNumber = "94",
    scryfallId = "2520ab23-a068-4462-b261-2754409b4108",
    artist = "Immanuela Crovius",
    imageUri = "https://cards.scryfall.io/normal/front/2/5/2520ab23-a068-4462-b261-2754409b4108.jpg?1748706117",
    releaseDate = "2025-06-13",
    rarity = Rarity.MYTHIC,
)
