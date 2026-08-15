package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Archway Angel reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RNA's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ArchwayAngelReprint = Printing(
    oracleId = "f2cf087a-d89a-4145-9394-945ce41c8cca",
    name = "Archway Angel",
    setCode = "FDN",
    collectorNumber = "566",
    artist = "Milivoj Ćeran",
    imageUri = "https://cards.scryfall.io/normal/front/2/c/2c3ff489-bdd7-4aeb-8130-3c234d0dd3dd.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
