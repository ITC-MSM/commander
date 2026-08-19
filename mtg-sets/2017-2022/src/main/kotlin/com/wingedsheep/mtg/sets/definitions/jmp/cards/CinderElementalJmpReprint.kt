package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cinder Elemental reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Mercadian Masques's `cards/` package; this file contributes only presentation data.
 */
val CinderElementalJmpReprint = Printing(
    oracleId = "01805d0d-3a72-4d26-9475-3e68c278b7fe",
    name = "Cinder Elemental",
    setCode = "JMP",
    collectorNumber = "304",
    scryfallId = "78c3c616-1f95-41b1-a624-79d6362d4f16",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/7/8/78c3c616-1f95-41b1-a624-79d6362d4f16.jpg?1783930398",
    releaseDate = "2020-07-17",
    rarity = Rarity.UNCOMMON,
)
