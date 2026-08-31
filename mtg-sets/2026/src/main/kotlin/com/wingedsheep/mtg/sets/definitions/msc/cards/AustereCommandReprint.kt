package com.wingedsheep.mtg.sets.definitions.msc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Austere Command reprint in MSC. The canonical CardDefinition lives in Lorwyn (`lrw`), the card's
 * earliest real printing; this file contributes only per-printing presentation data.
 */
val AustereCommandReprint = Printing(
    oracleId = "09cc8709-fe10-472a-b05c-e89f3523018d",
    name = "Austere Command",
    setCode = "MSC",
    collectorNumber = "121",
    scryfallId = "a8bc7912-e201-468a-b251-140205cb741c",
    artist = "Taurin Clarke",
    imageUri = "https://cards.scryfall.io/normal/front/a/8/a8bc7912-e201-468a-b251-140205cb741c.jpg?1783903253",
    releaseDate = "2026-06-26",
    rarity = Rarity.RARE,
)
