package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Blinding Light reprint in Invasion.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Mirage's `cards/`
 * package (earliest real printing); this file contributes only the Invasion-specific
 * presentation row.
 */
val BlindingLightReprint = Printing(
    oracleId = "6b315dc3-c330-4b30-b6ad-4da12ccf6ca3",
    name = "Blinding Light",
    setCode = "INV",
    collectorNumber = "9",
    scryfallId = "882c1e15-b508-4885-9626-4c8d2598a006",
    artist = "Marc Fishman",
    imageUri = "https://cards.scryfall.io/normal/front/8/8/882c1e15-b508-4885-9626-4c8d2598a006.jpg?1562922528",
    releaseDate = "2000-10-02",
    rarity = Rarity.UNCOMMON,
)
