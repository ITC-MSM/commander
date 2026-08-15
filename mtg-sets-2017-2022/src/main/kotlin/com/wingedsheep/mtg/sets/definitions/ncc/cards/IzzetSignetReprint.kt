package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Izzet Signet reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Guildpact's `cards/` package; this file
 * contributes only the New Capenna Commander presentation row.
 */
val IzzetSignetReprint = Printing(
    oracleId = "2fda4fe7-8b0c-489c-a000-6d358e614e34",
    name = "Izzet Signet",
    setCode = "NCC",
    collectorNumber = "369",
    scryfallId = "932dff99-8cc1-4ce5-951c-974bd2632dd2",
    artist = "Raoul Vitale",
    imageUri = "https://cards.scryfall.io/normal/front/9/3/932dff99-8cc1-4ce5-951c-974bd2632dd2.jpg?1673485368",
    releaseDate = "2022-04-29",
    rarity = Rarity.COMMON,
)
