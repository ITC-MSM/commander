package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Castle Embereth reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Throne of Eldraine's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val CastleEmberethReprint = Printing(
    oracleId = "91fbb25b-8521-483f-88b0-77778d25f7fd",
    name = "Castle Embereth",
    setCode = "NCC",
    collectorNumber = "392",
    scryfallId = "a42a8db8-bc8d-47f6-a1d4-78adfcbdd19e",
    artist = "Jaime Jones",
    imageUri = "https://cards.scryfall.io/normal/front/a/4/a42a8db8-bc8d-47f6-a1d4-78adfcbdd19e.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
