package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Arms Dealer reprint in J22. Canonical CardDefinition lives in Mercadian Masques (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.mmq.cards.ArmsDealer`.
 */
val ArmsDealerReprint = Printing(
    oracleId = "8ebc4198-7317-4ffb-b8b8-14733c2077ff",
    name = "Arms Dealer",
    setCode = "J22",
    collectorNumber = "493",
    scryfallId = "aa460a11-afd1-4997-89ac-6a64af63db55",
    artist = "Wayne Reynolds",
    imageUri = "https://cards.scryfall.io/normal/front/a/a/aa460a11-afd1-4997-89ac-6a64af63db55.jpg?1783918965",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
