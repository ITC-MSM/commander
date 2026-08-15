package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swiftwater Cliffs reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SwiftwaterCliffsReprint = Printing(
    oracleId = "2f4ad084-2062-44c0-9975-15f100204531",
    name = "Swiftwater Cliffs",
    setCode = "MOM",
    collectorNumber = "273",
    scryfallId = "957efc4e-c2a9-46a2-b9e3-20dc419ffd05",
    artist = "Julian Kok Joon Wen",
    imageUri = "https://cards.scryfall.io/normal/front/9/5/957efc4e-c2a9-46a2-b9e3-20dc419ffd05.jpg?1682205892",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
