package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Oblivion Ring reprint in CMD. The canonical CardDefinition lives in Lorwyn (`lrw`), the card's
 * earliest real printing; this file contributes only per-printing presentation data.
 */
val OblivionRingReprint = Printing(
    oracleId = "bd9b9772-f5f9-4c6b-913e-7193bea5d0a7",
    name = "Oblivion Ring",
    setCode = "CMD",
    collectorNumber = "23",
    scryfallId = "892cf23c-e215-4978-b9b6-ef6e337f88be",
    artist = "Franz Vohwinkel",
    imageUri = "https://cards.scryfall.io/normal/front/8/9/892cf23c-e215-4978-b9b6-ef6e337f88be.jpg?1783941249",
    releaseDate = "2011-06-17",
    rarity = Rarity.COMMON,
)
