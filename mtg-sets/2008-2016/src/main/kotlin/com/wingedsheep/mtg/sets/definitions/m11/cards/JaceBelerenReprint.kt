package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jace Beleren reprint in Magic 2011 (M11). The canonical CardDefinition lives in
 * Lorwyn (`lrw`), the card's earliest real printing; this file contributes only
 * per-printing presentation data.
 */
val JaceBelerenReprint = Printing(
    oracleId = "cc2b6e58-2d6f-495e-95f3-9496105c4cba",
    name = "Jace Beleren",
    setCode = "M11",
    collectorNumber = "58",
    scryfallId = "441af393-2f70-4765-ae95-730c1e1d864f",
    artist = "Aleksi Briclot",
    imageUri = "https://cards.scryfall.io/normal/front/4/4/441af393-2f70-4765-ae95-730c1e1d864f.jpg?1783941824",
    releaseDate = "2010-07-16",
    rarity = Rarity.MYTHIC,
)
