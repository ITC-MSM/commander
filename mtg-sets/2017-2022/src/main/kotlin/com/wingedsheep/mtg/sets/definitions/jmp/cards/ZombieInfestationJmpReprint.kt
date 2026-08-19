package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Zombie Infestation reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Odyssey's `cards/` package; this file contributes only presentation data.
 */
val ZombieInfestationJmpReprint = Printing(
    oracleId = "bdce1af0-3643-4e77-88f9-320206a191d4",
    name = "Zombie Infestation",
    setCode = "JMP",
    collectorNumber = "288",
    scryfallId = "c9ce5007-56ab-4361-8130-df48add1492b",
    artist = "Thomas M. Baxa",
    imageUri = "https://cards.scryfall.io/normal/front/c/9/c9ce5007-56ab-4361-8130-df48add1492b.jpg?1783930404",
    releaseDate = "2020-07-17",
    rarity = Rarity.UNCOMMON,
)
