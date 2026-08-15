package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gnarlid Colony reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in the ZNR (Zendikar Rising)
 * `cards/` package — its earliest real printing. This file contributes only the FDN presentation
 * row, picked up automatically by `CardDiscovery.findPrintingsIn`.
 */
val GnarlidColonyReprint = Printing(
    oracleId = "e5481431-c952-4ad9-94fe-355076ef632b",
    name = "Gnarlid Colony",
    setCode = "FDN",
    collectorNumber = "224",
    scryfallId = "47565d10-96bf-4fb0-820f-f20a44a76b6f",
    artist = "Izzy",
    imageUri = "https://cards.scryfall.io/normal/front/4/7/47565d10-96bf-4fb0-820f-f20a44a76b6f.jpg?1782689074",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
