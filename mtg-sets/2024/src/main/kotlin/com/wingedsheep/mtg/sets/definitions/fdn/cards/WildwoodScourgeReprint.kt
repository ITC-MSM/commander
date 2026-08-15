package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wildwood Scourge reprint in FDN. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Core Set 2021 (its earliest printing);
 * this file contributes only the FDN presentation row.
 */
val WildwoodScourgeReprint = Printing(
    oracleId = "b9dec104-c636-4770-a7fc-7a3331face15",
    name = "Wildwood Scourge",
    setCode = "FDN",
    collectorNumber = "236",
    scryfallId = "36359fb6-fb8c-4382-8555-e348422f116c",
    artist = "Bryan Sola",
    imageUri = "https://cards.scryfall.io/normal/front/3/6/36359fb6-fb8c-4382-8555-e348422f116c.jpg?1783909054",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
