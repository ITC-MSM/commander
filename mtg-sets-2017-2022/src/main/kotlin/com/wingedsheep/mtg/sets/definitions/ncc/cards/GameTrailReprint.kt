package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Game Trail reprint in NCC (Streets of New Capenna Commander).
 *
 * Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * `definitions/soi/cards/GameTrail.kt`. This file contributes only the NCC-specific
 * presentation row; `CardDiscovery.findPrintingsIn` surfaces it via the set's
 * `printings`.
 */
val GameTrailReprint = Printing(
    oracleId = "00de57d2-7cb6-4337-9bc6-f6711e4dfabf",
    name = "Game Trail",
    setCode = "NCC",
    collectorNumber = "405",
    scryfallId = "5b9d142b-bc3f-4d2d-8ca0-24148813300d",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/5/b/5b9d142b-bc3f-4d2d-8ca0-24148813300d.jpg?1673485740",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
