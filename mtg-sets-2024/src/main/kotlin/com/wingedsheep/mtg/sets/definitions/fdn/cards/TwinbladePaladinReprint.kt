package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Twinblade Paladin reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in M20's
 * `cards/` package (the card's earliest real printing). This file contributes only the
 * FDN-specific presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TwinbladePaladinReprint = Printing(
    oracleId = "d373dc71-5196-43a0-8cc5-67e2a591aad0",
    name = "Twinblade Paladin",
    setCode = "FDN",
    collectorNumber = "503",
    scryfallId = "5cd9e73d-de8d-486f-bbbd-a2f5d3f8f686",
    artist = "Jana Schirmer",
    imageUri = "https://cards.scryfall.io/normal/front/5/c/5cd9e73d-de8d-486f-bbbd-a2f5d3f8f686.jpg?1782688827",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
