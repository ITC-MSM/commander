package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Lyra Dawnbringer reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val LyraDawnbringerReprint = Printing(
    oracleId = "592c91fc-6430-4c76-9460-65f047350f67",
    name = "Lyra Dawnbringer",
    setCode = "FDN",
    collectorNumber = "738",
    scryfallId = "f9fa30b6-3a33-46fd-8b32-ac1cfa41500d",
    artist = "Chris Rahn",
    imageUri = "https://cards.scryfall.io/normal/front/f/9/f9fa30b6-3a33-46fd-8b32-ac1cfa41500d.jpg?1775599744",
    releaseDate = "2026-04-24",
    rarity = Rarity.MYTHIC,
)
