package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Oriflamme reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MH1's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GoblinOriflammeReprint = Printing(
    oracleId = "836bd011-2da5-443a-a814-19a664b98a1a",
    name = "Goblin Oriflamme",
    setCode = "FDN",
    collectorNumber = "539",
    artist = "David Palumbo",
    imageUri = "https://cards.scryfall.io/normal/front/0/2/0259e4d8-849b-468e-a902-aa1d7f2d5b6a.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
