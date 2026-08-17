package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodbraid Elf reprint in CLB. The canonical CardDefinition lives in
 * Alara Reborn (`arb`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val BloodbraidElfReprint = Printing(
    oracleId = "3f0c9466-5ab9-4205-a84f-b4b27b5a678e",
    name = "Bloodbraid Elf",
    setCode = "CLB",
    collectorNumber = "839",
    scryfallId = "8174d8dc-ae0f-469f-a773-cb294540ea25",
    artist = "Raymond Swanland",
    imageUri = "https://cards.scryfall.io/normal/front/8/1/8174d8dc-ae0f-469f-a773-cb294540ea25.jpg?1783922400",
    releaseDate = "2022-06-10",
    rarity = Rarity.UNCOMMON,
)
