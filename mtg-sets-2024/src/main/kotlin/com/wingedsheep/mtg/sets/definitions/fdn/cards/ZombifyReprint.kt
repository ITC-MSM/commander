package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Zombify reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in
 * the Odyssey (ODY) set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ZombifyReprint = Printing(
    oracleId = "bb95db4d-5017-4121-bf79-d68476602d8c",
    name = "Zombify",
    setCode = "FDN",
    collectorNumber = "187",
    scryfallId = "dc798e6f-13c4-457c-b052-b7b65bc83cfe",
    artist = "Jason A. Engle",
    imageUri = "https://cards.scryfall.io/normal/front/d/c/dc798e6f-13c4-457c-b052-b7b65bc83cfe.jpg?1730489291",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
