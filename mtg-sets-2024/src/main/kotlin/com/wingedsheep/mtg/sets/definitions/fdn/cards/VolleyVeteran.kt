package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Volley Veteran reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VolleyVeteranReprint = Printing(
    oracleId = "bf4268e6-170a-445b-b215-718f7494ae28",
    name = "Volley Veteran",
    setCode = "FDN",
    collectorNumber = "550",
    artist = "Olivier Bernard",
    imageUri = "https://cards.scryfall.io/normal/front/7/f/7fc914fe-e699-4a21-aa09-f3573d020b87.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
