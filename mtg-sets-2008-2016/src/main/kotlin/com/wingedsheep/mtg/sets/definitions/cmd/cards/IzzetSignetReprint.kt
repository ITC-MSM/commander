package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Izzet Signet reprint in Commander 2011. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Guildpact's `cards/` package; this file
 * contributes only the Commander 2011 presentation row.
 */
val IzzetSignetReprint = Printing(
    oracleId = "2fda4fe7-8b0c-489c-a000-6d358e614e34",
    name = "Izzet Signet",
    setCode = "CMD",
    collectorNumber = "252",
    scryfallId = "ff0a314e-4705-4777-a44c-fdf7ec5171fd",
    artist = "Greg Hildebrandt",
    imageUri = "https://cards.scryfall.io/normal/front/f/f/ff0a314e-4705-4777-a44c-fdf7ec5171fd.jpg?1592714452",
    releaseDate = "2011-06-17",
    rarity = Rarity.COMMON,
)
