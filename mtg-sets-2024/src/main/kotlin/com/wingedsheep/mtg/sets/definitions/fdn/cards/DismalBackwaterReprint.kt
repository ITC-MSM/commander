package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dismal Backwater reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DismalBackwaterReprint = Printing(
    oracleId = "865a2194-fca0-446e-aae3-ca475cd66e00",
    name = "Dismal Backwater",
    setCode = "FDN",
    collectorNumber = "261",
    scryfallId = "dbb0df36-8467-4a41-8e1c-6c3584d4fd10",
    artist = "Sam Burley",
    imageUri = "https://cards.scryfall.io/normal/front/d/b/dbb0df36-8467-4a41-8e1c-6c3584d4fd10.jpg?1730489576",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
