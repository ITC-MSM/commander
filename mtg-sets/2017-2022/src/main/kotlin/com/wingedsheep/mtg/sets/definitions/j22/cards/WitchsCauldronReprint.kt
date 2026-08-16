package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Witch's Cauldron reprint in J22. Canonical CardDefinition lives in Core Set 2021 (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.m21.cards.WitchsCauldron`.
 */
val WitchsCauldronReprint = Printing(
    oracleId = "cb65ee08-bfdb-4c18-8967-8bcf31fcabfa",
    name = "Witch's Cauldron",
    setCode = "J22",
    collectorNumber = "490",
    scryfallId = "27d11924-23c5-485e-9c56-9efcaf6cd9d2",
    artist = "Jason A. Engle",
    imageUri = "https://cards.scryfall.io/normal/front/2/7/27d11924-23c5-485e-9c56-9efcaf6cd9d2.jpg?1783918967",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
