package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mind Stone reprint in CLB. The canonical CardDefinition lives in
 * Weatherlight (`wth`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val MindStoneReprint = Printing(
    oracleId = "c97361b5-af16-4a7b-af85-a429dbaf4ad2",
    name = "Mind Stone",
    setCode = "CLB",
    collectorNumber = "325",
    scryfallId = "1969ddc0-ee6c-4c3d-a9dd-7f1c491609be",
    artist = "Ioannis Fiore",
    imageUri = "https://cards.scryfall.io/normal/front/1/9/1969ddc0-ee6c-4c3d-a9dd-7f1c491609be.jpg?1783922670",
    releaseDate = "2022-06-10",
    rarity = Rarity.UNCOMMON,
)
