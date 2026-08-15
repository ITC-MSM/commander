package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Reckless Spite reprint in Invasion.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Tempest's
 * `cards/` package; this file contributes only the Invasion-specific presentation row.
 */
val RecklessSpiteReprint = Printing(
    oracleId = "a684df3a-5441-4daa-86d1-c47a91b35e6a",
    name = "Reckless Spite",
    setCode = "INV",
    collectorNumber = "121",
    scryfallId = "2412497b-cae5-444d-9beb-7761d15cd5c5",
    artist = "Chippy",
    imageUri = "https://cards.scryfall.io/normal/front/2/4/2412497b-cae5-444d-9beb-7761d15cd5c5.jpg?1562902258",
    releaseDate = "2000-10-02",
    rarity = Rarity.UNCOMMON,
)
