package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fire Elemental reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FireElementalReprint = Printing(
    oracleId = "3912d21e-1ebc-4a81-9dc9-f404248d564a",
    name = "Fire Elemental",
    setCode = "FDN",
    collectorNumber = "538",
    scryfallId = "dc506f58-048d-49cc-ad8c-2eb851b08bb6",
    artist = "Joe Slucher",
    imageUri = "https://cards.scryfall.io/normal/front/d/c/dc506f58-048d-49cc-ad8c-2eb851b08bb6.jpg?1730490639",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
