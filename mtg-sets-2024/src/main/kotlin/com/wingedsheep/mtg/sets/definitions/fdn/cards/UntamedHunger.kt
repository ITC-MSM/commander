package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Untamed Hunger reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * OGW's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val UntamedHungerReprint = Printing(
    oracleId = "550abd9b-9aae-4812-9bc0-7f131d6dcde8",
    name = "Untamed Hunger",
    setCode = "FDN",
    collectorNumber = "529",
    artist = "Willian Murai",
    imageUri = "https://cards.scryfall.io/normal/front/0/5/05937911-897b-4638-9536-2e463884f453.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
