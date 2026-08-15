package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Breath of Darigaaz reprint in Commander 2011.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Invasion's `cards/`
 * package; this file contributes only the Commander 2011 presentation row.
 */
val BreathOfDarigaazReprint = Printing(
    oracleId = "1954994f-17bf-4ea5-af72-60f9bfcb6569",
    name = "Breath of Darigaaz",
    setCode = "CMD",
    collectorNumber = "112",
    scryfallId = "7fcfa18a-72c5-43cf-8d90-308a73407082",
    artist = "Greg Hildebrandt & Tim Hildebrandt",
    imageUri = "https://cards.scryfall.io/normal/front/7/f/7fcfa18a-72c5-43cf-8d90-308a73407082.jpg?1592713478",
    releaseDate = "2011-06-17",
    rarity = Rarity.UNCOMMON,
)
