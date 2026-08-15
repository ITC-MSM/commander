package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Serra Angel reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SerraAngelReprint = Printing(
    oracleId = "4b7ac066-e5c7-43e6-9e7e-2739b24a905d",
    name = "Serra Angel",
    setCode = "FDN",
    collectorNumber = "740",
    scryfallId = "b8c5e74c-96e7-4a1f-93b7-14d776fe4b2d",
    artist = "Greg Staples",
    imageUri = "https://cards.scryfall.io/normal/front/b/8/b8c5e74c-96e7-4a1f-93b7-14d776fe4b2d.jpg?1775599758",
    releaseDate = "2026-04-24",
    rarity = Rarity.UNCOMMON,
)
