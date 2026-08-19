package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Child of Night reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Magic 2010's `cards/` package; this file contributes only presentation data.
 */
val ChildOfNightJmpReprint = Printing(
    oracleId = "c650a7bc-e350-44a0-a698-d4a233d66156",
    name = "Child of Night",
    setCode = "JMP",
    collectorNumber = "218",
    scryfallId = "3887af00-a87d-4396-b82b-38b88c084e8e",
    artist = "Igor Kieryluk",
    imageUri = "https://cards.scryfall.io/normal/front/3/8/3887af00-a87d-4396-b82b-38b88c084e8e.jpg?1783930429",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
