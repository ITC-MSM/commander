package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Aura Shards reprint in Commander 2011.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Invasion's `cards/`
 * package; this file contributes only the Commander 2011 presentation row.
 */
val AuraShardsReprint = Printing(
    oracleId = "8d03d050-391c-4311-8c42-4ee632d40fdc",
    name = "Aura Shards",
    setCode = "CMD",
    collectorNumber = "182",
    scryfallId = "a6222cc6-996e-4b73-af87-e837bf1eb921",
    artist = "Ron Spencer",
    imageUri = "https://cards.scryfall.io/normal/front/a/6/a6222cc6-996e-4b73-af87-e837bf1eb921.jpg?1745319955",
    releaseDate = "2011-06-17",
    rarity = Rarity.UNCOMMON,
)
