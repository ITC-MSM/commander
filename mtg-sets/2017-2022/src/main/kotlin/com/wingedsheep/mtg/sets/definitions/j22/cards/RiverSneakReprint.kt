package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * River Sneak reprint in J22. Canonical CardDefinition lives in Ixalan (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.xln.cards.RiverSneak`.
 */
val RiverSneakReprint = Printing(
    oracleId = "678095cc-ec8f-471a-b675-e59905d65c33",
    name = "River Sneak",
    setCode = "J22",
    collectorNumber = "339",
    scryfallId = "cac32ebc-c7c6-49fd-a2e3-1ace1bc979ae",
    artist = "Slawomir Maniak",
    imageUri = "https://cards.scryfall.io/normal/front/c/a/cac32ebc-c7c6-49fd-a2e3-1ace1bc979ae.jpg?1783919042",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
