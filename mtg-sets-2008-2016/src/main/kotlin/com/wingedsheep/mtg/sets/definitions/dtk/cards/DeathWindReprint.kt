package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Death Wind reprint in Dragons of Tarkir. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Avacyn Restored's `cards/`
 * package; this file contributes only presentation data.
 */
val DeathWindReprint = Printing(
    oracleId = "a636b62a-340b-444d-9161-f6d3367b745c",
    name = "Death Wind",
    setCode = "DTK",
    collectorNumber = "95",
    scryfallId = "87ed0a14-1a98-4190-b195-f84fa42d4364",
    artist = "Nils Hamm",
    imageUri = "https://cards.scryfall.io/normal/front/8/7/87ed0a14-1a98-4190-b195-f84fa42d4364.jpg?1562789406",
    releaseDate = "2015-03-27",
    rarity = Rarity.UNCOMMON,
)
