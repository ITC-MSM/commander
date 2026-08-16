package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Neutralize reprint in J22. Canonical CardDefinition lives in Ikoria: Lair of Behemoths (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.iko.cards.Neutralize`.
 */
val NeutralizeReprint = Printing(
    oracleId = "c2c60973-b4d5-43fa-a0c4-b5688b28df05",
    name = "Neutralize",
    setCode = "J22",
    collectorNumber = "327",
    scryfallId = "3a897be4-95e2-4fab-bd08-565afe19533b",
    artist = "Yongjae Choi",
    imageUri = "https://cards.scryfall.io/normal/front/3/a/3a897be4-95e2-4fab-bd08-565afe19533b.jpg?1783919048",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
