package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Deadly Plot reprint in FDN. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Jumpstart 2022 (its earliest printing);
 * this file contributes only the FDN presentation row.
 */
val DeadlyPlotReprint = Printing(
    oracleId = "6178715c-870d-4710-b758-66e080804ee3",
    name = "Deadly Plot",
    setCode = "FDN",
    collectorNumber = "520",
    scryfallId = "b32fddc3-a38f-4eea-ae01-4158e3cbca6c",
    artist = "Peter Polach",
    imageUri = "https://cards.scryfall.io/normal/front/b/3/b32fddc3-a38f-4eea-ae01-4158e3cbca6c.jpg?1783908959",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
