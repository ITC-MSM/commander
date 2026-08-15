package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Electrolyze reprint in Commander 2011. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Guildpact's `cards/` package; this file
 * contributes only the Commander 2011 presentation row.
 */
val ElectrolyzeReprint = Printing(
    oracleId = "07b222d7-24f2-4994-9004-ff6672ebe161",
    name = "Electrolyze",
    setCode = "CMD",
    collectorNumber = "197",
    scryfallId = "dfd8ac90-48fa-4107-8671-316bb2eb81f5",
    artist = "Zoltan Boros & Gabor Szikszai",
    imageUri = "https://cards.scryfall.io/normal/front/d/f/dfd8ac90-48fa-4107-8671-316bb2eb81f5.jpg?1592714039",
    releaseDate = "2011-06-17",
    rarity = Rarity.UNCOMMON,
)
