package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Alloy Myr reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * New Phyrexia's `cards/` package; this file contributes only presentation data.
 */
val AlloyMyrJmpReprint = Printing(
    oracleId = "efb0394c-2a45-4dd8-bca3-08704056fa31",
    name = "Alloy Myr",
    setCode = "JMP",
    collectorNumber = "457",
    scryfallId = "e8d2180b-f54c-47a9-9458-28e7a19e35ee",
    artist = "Matt Cavotta",
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e8d2180b-f54c-47a9-9458-28e7a19e35ee.jpg?1783930344",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
