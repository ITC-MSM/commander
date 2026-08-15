package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Angel of Vitality reprint in FDN. Canonical CardDefinition lives in Core Set 2020 (its
 * earliest real printing), `com.wingedsheep.mtg.sets.definitions.m20.cards.AngelOfVitality`.
 */
val AngelOfVitalityReprint = Printing(
    oracleId = "635fe908-6f58-4c9e-ac55-0775a7c6f278",
    name = "Angel of Vitality",
    setCode = "FDN",
    collectorNumber = "706",
    scryfallId = "c279947e-168f-4af5-902b-aba724f52b8a",
    artist = "Johannes Voss",
    imageUri = "https://cards.scryfall.io/normal/front/c/2/c279947e-168f-4af5-902b-aba724f52b8a.jpg?1782688653",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
