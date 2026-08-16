package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Auramancer reprint in J22. Canonical CardDefinition lives in Odyssey (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.ody.cards.Auramancer`.
 */
val AuramancerReprint = Printing(
    oracleId = "bd5eb181-6a69-4dc2-93a0-fa000291bc3d",
    name = "Auramancer",
    setCode = "J22",
    collectorNumber = "153",
    scryfallId = "d6e3b21e-a05c-4603-b4d1-68353273d4a9",
    artist = "Rebecca Guay",
    imageUri = "https://cards.scryfall.io/normal/front/d/6/d6e3b21e-a05c-4603-b4d1-68353273d4a9.jpg?1783919130",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
