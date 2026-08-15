package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ravenous Rats reprint in Invasion.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Portal Second Age's
 * `cards/` package; this file contributes only the Invasion-specific presentation row.
 */
val RavenousRatsReprint = Printing(
    oracleId = "2fa1bbfd-92b5-482c-b32d-4cdc286474c4",
    name = "Ravenous Rats",
    setCode = "INV",
    collectorNumber = "120",
    scryfallId = "89e29069-add5-4099-b800-9f1e4402cc1a",
    artist = "Tom Wänerstrand",
    imageUri = "https://cards.scryfall.io/normal/front/8/9/89e29069-add5-4099-b800-9f1e4402cc1a.jpg?1562922876",
    releaseDate = "2000-10-02",
    rarity = Rarity.COMMON,
)
