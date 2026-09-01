package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Garruk Wildspeaker reprint in Commander (CMD). The canonical CardDefinition lives in
 * Lorwyn (`lrw`), the card's earliest real printing; this file contributes only
 * per-printing presentation data.
 */
val GarrukWildspeakerReprint = Printing(
    oracleId = "186b4def-4fff-4a2b-bcbf-5318b0ac5fa9",
    name = "Garruk Wildspeaker",
    setCode = "CMD",
    collectorNumber = "157",
    scryfallId = "08cf7ee6-ba30-42cd-a5b3-fd2fe7252f11",
    artist = "Aleksi Briclot",
    imageUri = "https://cards.scryfall.io/normal/front/0/8/08cf7ee6-ba30-42cd-a5b3-fd2fe7252f11.jpg?1783941195",
    releaseDate = "2011-06-17",
    rarity = Rarity.MYTHIC,
)
