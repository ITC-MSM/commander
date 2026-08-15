package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Balmor, Battlemage Captain reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DMU's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BalmorBattlemageCaptainReprint = Printing(
    oracleId = "7da19fc5-cb85-4ec7-beea-95210378bbfe",
    name = "Balmor, Battlemage Captain",
    setCode = "FDN",
    collectorNumber = "237",
    artist = "Bram Sels",
    imageUri = "https://cards.scryfall.io/normal/front/0/b/0b45ab13-9bb6-48af-8b37-d97b25801ac8.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
