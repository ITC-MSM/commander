package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Feast of Blood reprint in J22. Canonical CardDefinition lives in Zendikar (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.zen.cards.FeastOfBlood`.
 */
val FeastOfBloodReprint = Printing(
    oracleId = "fed05740-01b2-4c7a-8b97-55e64837c07f",
    name = "Feast of Blood",
    setCode = "J22",
    collectorNumber = "118",
    scryfallId = "f576f3b6-59c5-4710-b238-7fb2bbcf563e",
    artist = "Irina Nordsol",
    imageUri = "https://cards.scryfall.io/normal/front/f/5/f576f3b6-59c5-4710-b238-7fb2bbcf563e.jpg?1783919145",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
