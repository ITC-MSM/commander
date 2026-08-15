package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Joraga Invocation reprint in FDN. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Magic Origins (its earliest printing);
 * this file contributes only the FDN presentation row.
 */
val JoragaInvocationReprint = Printing(
    oracleId = "216d3eed-cb07-40c5-869a-962e074e757d",
    name = "Joraga Invocation",
    setCode = "FDN",
    collectorNumber = "555",
    scryfallId = "01ceca57-65f4-4f99-bd14-9fe5f86c1f62",
    artist = "Kieran Yanner",
    imageUri = "https://cards.scryfall.io/normal/front/0/1/01ceca57-65f4-4f99-bd14-9fe5f86c1f62.jpg?1783908947",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
