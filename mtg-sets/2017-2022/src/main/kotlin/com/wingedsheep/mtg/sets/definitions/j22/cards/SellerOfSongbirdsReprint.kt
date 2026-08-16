package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Seller of Songbirds reprint in J22. Canonical CardDefinition lives in Return to Ravnica (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.rtr.cards.SellerOfSongbirds`.
 */
val SellerOfSongbirdsReprint = Printing(
    oracleId = "87e4c6ff-f83f-412b-9d23-aac7a57ef6db",
    name = "Seller of Songbirds",
    setCode = "J22",
    collectorNumber = "240",
    scryfallId = "4f3b2176-958a-42e0-b88e-5b3416b40150",
    artist = "Christopher Moeller",
    imageUri = "https://cards.scryfall.io/normal/front/4/f/4f3b2176-958a-42e0-b88e-5b3416b40150.jpg?1783919088",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
