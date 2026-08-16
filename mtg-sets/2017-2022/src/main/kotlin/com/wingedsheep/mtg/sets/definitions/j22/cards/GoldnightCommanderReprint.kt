package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Goldnight Commander reprint in J22. Canonical CardDefinition lives in Avacyn Restored (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.avr.cards.GoldnightCommander`.
 */
val GoldnightCommanderReprint = Printing(
    oracleId = "3dc51dfc-7a60-41bf-b944-6afad6b06c22",
    name = "Goldnight Commander",
    setCode = "J22",
    collectorNumber = "192",
    scryfallId = "889f5a07-3b5c-494b-834a-3a5e444e38cc",
    artist = "Chris Rahn",
    imageUri = "https://cards.scryfall.io/normal/front/8/8/889f5a07-3b5c-494b-834a-3a5e444e38cc.jpg?1783919111",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
