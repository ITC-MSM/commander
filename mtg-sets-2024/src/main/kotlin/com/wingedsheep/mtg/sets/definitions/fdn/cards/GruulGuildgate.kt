package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gruul Guildgate reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GTC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GruulGuildgateReprint = Printing(
    oracleId = "d38476e9-2e47-4c0c-8129-483c0bd09ec0",
    name = "Gruul Guildgate",
    setCode = "FDN",
    collectorNumber = "690",
    artist = "Randy Gallegos",
    imageUri = "https://cards.scryfall.io/normal/front/3/a/3ab6c240-c97d-4a5c-bc39-860c2d9901c2.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
