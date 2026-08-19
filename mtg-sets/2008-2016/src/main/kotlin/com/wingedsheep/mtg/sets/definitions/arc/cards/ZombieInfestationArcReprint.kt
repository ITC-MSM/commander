package com.wingedsheep.mtg.sets.definitions.arc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Zombie Infestation reprint in Archenemy. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives
 * in another set's `cards/` package; this file contributes only presentation data.
 */
val ZombieInfestationArcReprint = Printing(
    oracleId = "bdce1af0-3643-4e77-88f9-320206a191d4",
    name = "Zombie Infestation",
    setCode = "ARC",
    collectorNumber = "28",
    scryfallId = "e1078bef-8caf-4dc5-a055-e64314501b23",
    artist = "Thomas M. Baxa",
    imageUri = "https://cards.scryfall.io/normal/front/e/1/e1078bef-8caf-4dc5-a055-e64314501b23.jpg?1783941911",
    releaseDate = "2010-06-18",
    rarity = Rarity.UNCOMMON,
)
