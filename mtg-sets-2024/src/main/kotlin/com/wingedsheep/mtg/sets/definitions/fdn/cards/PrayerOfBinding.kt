package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Prayer of Binding reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DMU's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PrayerOfBindingReprint = Printing(
    oracleId = "4505ff9f-97f1-45d1-aea9-5590fefb4a4d",
    name = "Prayer of Binding",
    setCode = "FDN",
    collectorNumber = "739",
    artist = "Wylie Beckert",
    imageUri = "https://cards.scryfall.io/normal/front/8/d/8d05289c-d8de-4085-ac01-5dd8fd954d35.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
