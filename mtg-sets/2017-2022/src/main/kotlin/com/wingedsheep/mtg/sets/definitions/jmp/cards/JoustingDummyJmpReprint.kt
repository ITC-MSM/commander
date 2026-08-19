package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jousting Dummy reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Throne of Eldraine's `cards/` package; this file contributes only presentation data.
 */
val JoustingDummyJmpReprint = Printing(
    oracleId = "96eea161-8af0-452f-b992-576efdf58d88",
    name = "Jousting Dummy",
    setCode = "JMP",
    collectorNumber = "470",
    scryfallId = "3d0c95b0-7b63-40e8-92ad-5ae5ffd3c4c1",
    artist = "Milivoj Ćeran",
    imageUri = "https://cards.scryfall.io/normal/front/3/d/3d0c95b0-7b63-40e8-92ad-5ae5ffd3c4c1.jpg?1783930338",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
