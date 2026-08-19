package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Marauder's Axe reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Core Set 2019's `cards/` package; this file contributes only presentation data.
 */
val MaraudersAxeJmpReprint = Printing(
    oracleId = "5d0e49fa-5dfb-48c2-af97-fdfb788d5f40",
    name = "Marauder's Axe",
    setCode = "JMP",
    collectorNumber = "473",
    scryfallId = "49d8aa8a-3e87-42ac-9c79-2baec771c1ef",
    artist = "Mitchell Malloy",
    imageUri = "https://cards.scryfall.io/normal/front/4/9/49d8aa8a-3e87-42ac-9c79-2baec771c1ef.jpg?1783930337",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
