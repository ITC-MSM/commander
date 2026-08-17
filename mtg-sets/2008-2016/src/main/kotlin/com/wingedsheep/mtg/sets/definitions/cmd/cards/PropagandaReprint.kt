package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Propaganda reprint in CMD. The canonical CardDefinition lives in
 * Tempest (`tmp`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val PropagandaReprint = Printing(
    oracleId = "ea9709b6-4c37-4d5a-b04d-cd4c42e4f9dd",
    name = "Propaganda",
    setCode = "CMD",
    collectorNumber = "55",
    scryfallId = "48f9976c-e8a2-4f5a-a2d0-aa63b27c19dc",
    artist = "Jeff Miracola",
    imageUri = "https://cards.scryfall.io/normal/front/4/8/48f9976c-e8a2-4f5a-a2d0-aa63b27c19dc.jpg?1783941235",
    releaseDate = "2011-06-17",
    rarity = Rarity.UNCOMMON,
)
