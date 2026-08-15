package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dwynen, Gilt-Leaf Daen reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ORI's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DwynenGiltLeafDaenReprint = Printing(
    oracleId = "30d0d75f-e94c-460b-b957-9f1d655c0f65",
    name = "Dwynen, Gilt-Leaf Daen",
    setCode = "FDN",
    collectorNumber = "217",
    artist = "Johannes Voss",
    imageUri = "https://cards.scryfall.io/normal/front/0/1/01c00d7b-7fac-4f8c-a1ea-de2cf4d06627.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
