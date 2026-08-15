package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Boros Guildgate reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GTC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BorosGuildgateReprint = Printing(
    oracleId = "73c423b7-cab8-4e69-8070-9edbf96a6c2c",
    name = "Boros Guildgate",
    setCode = "FDN",
    collectorNumber = "684",
    artist = "Titus Lunter",
    imageUri = "https://cards.scryfall.io/normal/front/3/e/3e3c74ea-40e9-4ad9-a491-c208403b68ad.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
