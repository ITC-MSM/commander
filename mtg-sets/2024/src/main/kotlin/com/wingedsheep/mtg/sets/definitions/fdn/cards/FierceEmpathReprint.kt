package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fierce Empath reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FierceEmpathReprint = Printing(
    oracleId = "5104053a-d394-4b00-82a4-60fd9f051a6e",
    name = "Fierce Empath",
    setCode = "FDN",
    collectorNumber = "636",
    scryfallId = "28a1dd7f-2e42-4062-a941-b489b98a49fc",
    artist = "Johann Bodin",
    imageUri = "https://cards.scryfall.io/normal/front/2/8/28a1dd7f-2e42-4062-a941-b489b98a49fc.jpg?1730491011",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
