package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Evolving Wilds reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EvolvingWildsReprint = Printing(
    oracleId = "a75445d3-1303-4bb5-89ad-26ea93fecd48",
    name = "Evolving Wilds",
    setCode = "FDN",
    collectorNumber = "262",
    scryfallId = "3a0b9356-5b91-4542-8802-f0f7275238e1",
    artist = "Sam Burley",
    imageUri = "https://cards.scryfall.io/normal/front/3/a/3a0b9356-5b91-4542-8802-f0f7275238e1.jpg?1730489581",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
