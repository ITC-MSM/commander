package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gnarlback Rhino reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in M20's `cards/` package
 * (the card's earliest real printing). This file contributes only the FDN presentation row —
 * set, collector number, art — picked up automatically by `CardDiscovery.findPrintingsIn`.
 */
val GnarlbackRhinoReprint = Printing(
    oracleId = "1ae339fd-8e64-4e3a-8e27-e4993932ba62",
    name = "Gnarlback Rhino",
    setCode = "FDN",
    collectorNumber = "638",
    scryfallId = "6b638f3d-7eb6-4675-9538-4a87a7f75c43",
    artist = "YW Tang",
    imageUri = "https://cards.scryfall.io/normal/front/6/b/6b638f3d-7eb6-4675-9538-4a87a7f75c43.jpg?1782688709",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
