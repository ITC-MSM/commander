package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Good-Fortune Unicorn reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MH1's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GoodFortuneUnicornReprint = Printing(
    oracleId = "1e0cdff3-3ec5-41fc-8053-f072bae156b3",
    name = "Good-Fortune Unicorn",
    setCode = "FDN",
    collectorNumber = "240",
    artist = "Kee Lo",
    imageUri = "https://cards.scryfall.io/normal/front/e/a/eabbe163-2b15-42e3-89ce-7363e6250d3a.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
