package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vampire Neonate reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VampireNeonateReprint = Printing(
    oracleId = "c0045925-25bf-403c-b721-5f05ba30985b",
    name = "Vampire Neonate",
    setCode = "FDN",
    collectorNumber = "531",
    artist = "Daarken",
    imageUri = "https://cards.scryfall.io/normal/front/8/6/86d64a1d-cda4-4c76-8d58-ccab5a6859a0.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
