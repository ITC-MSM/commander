package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Biogenic Upgrade reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RNA's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BiogenicUpgradeReprint = Printing(
    oracleId = "30812870-87c0-469c-95a5-811d5a181e72",
    name = "Biogenic Upgrade",
    setCode = "FDN",
    collectorNumber = "553",
    artist = "Kev Fang",
    imageUri = "https://cards.scryfall.io/normal/front/e/f/ef20af5e-1ffe-426a-805a-ca4a6a122260.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
