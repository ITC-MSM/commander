package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Juggernaut reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val JuggernautReprint = Printing(
    oracleId = "4ac9116f-36bc-4d71-b696-d6ee064e1d58",
    name = "Juggernaut",
    setCode = "FDN",
    collectorNumber = "255",
    scryfallId = "f4468fff-cd6f-428c-b7a0-ff89f5bbea2e",
    artist = "Kev Walker",
    imageUri = "https://cards.scryfall.io/normal/front/f/4/f4468fff-cd6f-428c-b7a0-ff89f5bbea2e.jpg?1730489555",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
