package com.wingedsheep.mtg.sets.definitions.arc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ryusei, the Falling Star reprint in ARC. The canonical CardDefinition lives in
 * Champions of Kamigawa (`chk`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val RyuseiTheFallingStarReprint = Printing(
    oracleId = "5e99ba3b-dc2b-4c3c-84ce-228d4772cfee",
    name = "Ryusei, the Falling Star",
    setCode = "ARC",
    collectorNumber = "45",
    scryfallId = "f05861f1-f6f1-44fa-91e9-f7cfa66b38ff",
    artist = "Nottsuo",
    imageUri = "https://cards.scryfall.io/normal/front/f/0/f05861f1-f6f1-44fa-91e9-f7cfa66b38ff.jpg?1783941907",
    releaseDate = "2010-06-18",
    rarity = Rarity.RARE,
)
