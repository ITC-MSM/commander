package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tranquil Cove reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TranquilCoveReprint = Printing(
    oracleId = "5d641bf6-0f93-4189-8dc1-ec7ea446dade",
    name = "Tranquil Cove",
    setCode = "FDN",
    collectorNumber = "270",
    scryfallId = "7c9cabca-5bcc-4b97-b2ac-a345ad3ee43c",
    artist = "Jonas De Ro",
    imageUri = "https://cards.scryfall.io/normal/front/7/c/7c9cabca-5bcc-4b97-b2ac-a345ad3ee43c.jpg?1730489610",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
