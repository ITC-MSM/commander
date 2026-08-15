package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Deadly Riposte reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * BRO's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DeadlyRiposteReprint = Printing(
    oracleId = "e341478e-c3f8-45d9-9430-abe3a58fa2fc",
    name = "Deadly Riposte",
    setCode = "FDN",
    collectorNumber = "492",
    artist = "Olena Richards",
    imageUri = "https://cards.scryfall.io/normal/front/6/5/65f5804a-075b-41d8-9639-785cad5c0974.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
