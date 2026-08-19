package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Keeper of Fables reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Throne of Eldraine's `cards/` package; this file contributes only presentation data.
 */
val KeeperOfFablesJmpReprint = Printing(
    oracleId = "c8ca3116-e0f0-4e27-aa0f-99ed85927040",
    name = "Keeper of Fables",
    setCode = "JMP",
    collectorNumber = "407",
    scryfallId = "3c21c795-e455-4ecf-a7a2-8f204c114c81",
    artist = "Alex Konstad",
    imageUri = "https://cards.scryfall.io/normal/front/3/c/3c21c795-e455-4ecf-a7a2-8f204c114c81.jpg?1783930362",
    releaseDate = "2020-07-17",
    rarity = Rarity.UNCOMMON,
)
