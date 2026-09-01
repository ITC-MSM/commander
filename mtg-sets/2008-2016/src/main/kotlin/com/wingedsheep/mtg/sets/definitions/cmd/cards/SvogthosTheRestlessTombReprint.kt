package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Svogthos, the Restless Tomb reprint in Commander 2011. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the Ravnica: City of Guilds (`rav`) `cards/`
 * package (the card's earliest real printing); this file contributes only per-printing
 * presentation data.
 */
val SvogthosTheRestlessTombReprint = Printing(
    oracleId = "a34a70b8-02e5-4e8c-a9e7-b21c5a11dddf",
    name = "Svogthos, the Restless Tomb",
    setCode = "CMD",
    collectorNumber = "289",
    scryfallId = "67259116-7b5e-4340-858f-9905a479084d",
    artist = "Martina Pilcerova",
    imageUri = "https://cards.scryfall.io/normal/front/6/7/67259116-7b5e-4340-858f-9905a479084d.jpg?1783941142",
    releaseDate = "2011-06-17",
    rarity = Rarity.UNCOMMON,
)
