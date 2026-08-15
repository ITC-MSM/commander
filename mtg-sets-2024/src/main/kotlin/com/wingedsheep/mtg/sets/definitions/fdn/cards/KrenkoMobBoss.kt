package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Krenko, Mob Boss reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M13's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val KrenkoMobBossReprint = Printing(
    oracleId = "68418069-f615-40ef-ae0d-764192acae00",
    name = "Krenko, Mob Boss",
    setCode = "FDN",
    collectorNumber = "204",
    artist = "Lie Setiawan",
    imageUri = "https://cards.scryfall.io/normal/front/8/2/824b2d73-2151-4e5e-9f05-8f63e2bdcaa9.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
