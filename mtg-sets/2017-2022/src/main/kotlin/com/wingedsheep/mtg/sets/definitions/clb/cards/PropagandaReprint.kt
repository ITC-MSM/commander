package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Propaganda reprint in CLB. The canonical CardDefinition lives in
 * Tempest (`tmp`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val PropagandaReprint = Printing(
    oracleId = "ea9709b6-4c37-4d5a-b04d-cd4c42e4f9dd",
    name = "Propaganda",
    setCode = "CLB",
    collectorNumber = "730",
    scryfallId = "e5f293d7-9c2b-41cb-8e3c-dfc1daa6635f",
    artist = "Clint Cearley",
    imageUri = "https://cards.scryfall.io/normal/front/e/5/e5f293d7-9c2b-41cb-8e3c-dfc1daa6635f.jpg?1783922465",
    releaseDate = "2022-06-10",
    rarity = Rarity.UNCOMMON,
)
