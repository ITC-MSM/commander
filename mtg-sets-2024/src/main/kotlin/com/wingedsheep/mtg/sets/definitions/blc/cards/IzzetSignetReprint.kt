package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Izzet Signet reprint in Bloomburrow Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Guildpact's `cards/` package; this file
 * contributes only the Bloomburrow Commander presentation row.
 */
val IzzetSignetReprint = Printing(
    oracleId = "2fda4fe7-8b0c-489c-a000-6d358e614e34",
    name = "Izzet Signet",
    setCode = "BLC",
    collectorNumber = "278",
    scryfallId = "a39b4b15-cd3e-4d30-8de2-8e584524d018",
    artist = "Raoul Vitale",
    imageUri = "https://cards.scryfall.io/normal/front/a/3/a39b4b15-cd3e-4d30-8de2-8e584524d018.jpg?1779241848",
    releaseDate = "2024-08-02",
    rarity = Rarity.UNCOMMON,
)
