package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Immaculate Magistrate reprints in Commander Legends (CMR). The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Lorwyn (its earliest printing); these rows
 * contribute only presentation data. CMR printed two collector numbers: the main #234 and the
 * extended-art #679.
 */
val ImmaculateMagistrateReprint = Printing(
    oracleId = "e6e38bd4-e6dc-400b-8e08-956726842dc4",
    name = "Immaculate Magistrate",
    setCode = "CMR",
    collectorNumber = "234",
    scryfallId = "d9208db8-1ba5-4f9c-90a4-03e377ae6f86",
    artist = "Jim Nelson",
    imageUri = "https://cards.scryfall.io/normal/front/d/9/d9208db8-1ba5-4f9c-90a4-03e377ae6f86.jpg?1783928793",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
)

val ImmaculateMagistrateReprintExtended = Printing(
    oracleId = "e6e38bd4-e6dc-400b-8e08-956726842dc4",
    name = "Immaculate Magistrate",
    setCode = "CMR",
    collectorNumber = "679",
    scryfallId = "4b3ae08c-aa65-441e-a1cd-8bcb8bda5e33",
    artist = "Jim Nelson",
    imageUri = "https://cards.scryfall.io/normal/front/4/b/4b3ae08c-aa65-441e-a1cd-8bcb8bda5e33.jpg?1783928607",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
)
