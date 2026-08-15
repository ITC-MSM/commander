package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Golgari Guildgate reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RTR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GolgariGuildgateReprint = Printing(
    oracleId = "fa2da325-6859-45bb-b185-35526b01bcc1",
    name = "Golgari Guildgate",
    setCode = "FDN",
    collectorNumber = "689",
    artist = "Eytan Zana",
    imageUri = "https://cards.scryfall.io/normal/front/9/2/92d4646c-a375-4835-aa58-8bb77d1a5abf.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
