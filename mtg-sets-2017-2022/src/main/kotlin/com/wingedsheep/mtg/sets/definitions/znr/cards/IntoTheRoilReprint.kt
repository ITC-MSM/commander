package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Into the Roil reprints in Zendikar Rising — the regular (#62) and extended-art (#387) printings.
 * Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Zendikar (`zen`); these contribute
 * only presentation data.
 */
val IntoTheRoilReprint = Printing(
    oracleId = "c2898bbd-82a4-4d26-b6ee-169b0ebe71b4",
    name = "Into the Roil",
    setCode = "ZNR",
    collectorNumber = "62",
    scryfallId = "899540b0-c17a-4a73-912c-9b8925203de1",
    artist = "Campbell White",
    imageUri = "https://cards.scryfall.io/normal/front/8/9/899540b0-c17a-4a73-912c-9b8925203de1.jpg?1782706336",
    releaseDate = "2020-09-25",
    rarity = Rarity.COMMON,
)

val IntoTheRoilExtendedReprint = Printing(
    oracleId = "c2898bbd-82a4-4d26-b6ee-169b0ebe71b4",
    name = "Into the Roil",
    setCode = "ZNR",
    collectorNumber = "387",
    scryfallId = "a3c2c3d7-3437-44b8-8d39-d3abe02af50e",
    artist = "Campbell White",
    imageUri = "https://cards.scryfall.io/normal/front/a/3/a3c2c3d7-3437-44b8-8d39-d3abe02af50e.jpg?1782706100",
    releaseDate = "2020-09-25",
    rarity = Rarity.COMMON,
)
