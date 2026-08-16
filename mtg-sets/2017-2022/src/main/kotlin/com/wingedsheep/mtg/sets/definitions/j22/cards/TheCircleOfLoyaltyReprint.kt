package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * The Circle of Loyalty reprint in J22. Canonical CardDefinition lives in Throne of Eldraine (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.eld.cards.TheCircleOfLoyalty`.
 */
val TheCircleOfLoyaltyReprint = Printing(
    oracleId = "fd1b4523-2399-4771-89bf-1d65f5d67056",
    name = "The Circle of Loyalty",
    setCode = "J22",
    collectorNumber = "166",
    scryfallId = "29a37ed7-223e-4e19-b318-83cac54f6b16",
    artist = "Bastien L. Deharme",
    imageUri = "https://cards.scryfall.io/normal/front/2/9/29a37ed7-223e-4e19-b318-83cac54f6b16.jpg?1783919122",
    releaseDate = "2022-12-02",
    rarity = Rarity.MYTHIC,
)
