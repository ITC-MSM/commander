package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Skizzik reprint in Dominaria. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the Invasion set's `cards/`
 * package (Invasion is the card's earliest real printing); this file contributes
 * only presentation data for the Dominaria printing.
 */
val SkizzikReprint = Printing(
    oracleId = "0c77d37e-791c-415e-b369-55e93147ec34",
    name = "Skizzik",
    setCode = "DOM",
    collectorNumber = "145",
    scryfallId = "77af9d28-1639-47bd-b925-7f3d2eefd352",
    artist = "Tomasz Jedruszek",
    imageUri = "https://cards.scryfall.io/normal/front/7/7/77af9d28-1639-47bd-b925-7f3d2eefd352.jpg?1615334491",
    releaseDate = "2018-04-27",
    rarity = Rarity.UNCOMMON,
)
