package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swab Goblin reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SwabGoblinReprint = Printing(
    oracleId = "343ecf2f-0bbc-4840-8fb3-f5f61b26e76f",
    name = "Swab Goblin",
    setCode = "FDN",
    collectorNumber = "548",
    artist = "Josu Hernaiz",
    imageUri = "https://cards.scryfall.io/normal/front/8/d/8db11970-74c2-463d-88bb-9f88aa079eaa.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
