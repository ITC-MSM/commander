package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Child of Night reprint in Guilds of Ravnica. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives
 * in another set's `cards/` package; this file contributes only presentation data.
 */
val ChildOfNightGrnReprint = Printing(
    oracleId = "c650a7bc-e350-44a0-a698-d4a233d66156",
    name = "Child of Night",
    setCode = "GRN",
    collectorNumber = "65",
    scryfallId = "afebf1a5-eb9a-48c6-a26a-55b75408992b",
    artist = "Igor Kieryluk",
    imageUri = "https://cards.scryfall.io/normal/front/a/f/afebf1a5-eb9a-48c6-a26a-55b75408992b.jpg?1783934180",
    releaseDate = "2018-10-05",
    rarity = Rarity.COMMON,
)
