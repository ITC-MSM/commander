package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Blanchwood Armor reprint in FDN. Canonical CardDefinition lives in Urza's Saga (its
 * earliest real printing), `com.wingedsheep.mtg.sets.definitions.usg.cards.BlanchwoodArmor`.
 */
val BlanchwoodArmorReprint = Printing(
    oracleId = "80ea56ad-e741-4a85-b4e8-ce62e7d593d5",
    name = "Blanchwood Armor",
    setCode = "FDN",
    collectorNumber = "213",
    scryfallId = "1fd7ec1a-dafa-42ca-bc25-f6848fb03f60",
    artist = "Manuel Castañón",
    imageUri = "https://cards.scryfall.io/normal/front/1/f/1fd7ec1a-dafa-42ca-bc25-f6848fb03f60.jpg?1782689084",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
