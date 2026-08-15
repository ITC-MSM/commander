package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Battle-Rattle Shaman reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ROE's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BattleRattleShamanReprint = Printing(
    oracleId = "72dc807b-35fb-4d69-ae4c-04eed0646170",
    name = "Battle-Rattle Shaman",
    setCode = "FDN",
    collectorNumber = "533",
    artist = "Warren Mahy",
    imageUri = "https://cards.scryfall.io/normal/front/f/e/fee05f7e-d4da-4a72-8140-d505eecbdd32.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
