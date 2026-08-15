package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cuombajj Witches reprints in Commander Legends (CMR). The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Arabian Nights (its earliest printing); these
 * rows contribute only presentation data. CMR printed two collector numbers: the main #116 and the
 * extended-art #646.
 */
val CuombajjWitchesReprint = Printing(
    oracleId = "638eeb16-9e0e-4cc6-b97e-8ff0df81ca58",
    name = "Cuombajj Witches",
    setCode = "CMR",
    collectorNumber = "116",
    scryfallId = "6a26e910-275a-4981-831b-bfed936a7e3f",
    artist = "Seb McKinnon",
    imageUri = "https://cards.scryfall.io/normal/front/6/a/6a26e910-275a-4981-831b-bfed936a7e3f.jpg?1608909638",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)

val CuombajjWitchesReprintExtended = Printing(
    oracleId = "638eeb16-9e0e-4cc6-b97e-8ff0df81ca58",
    name = "Cuombajj Witches",
    setCode = "CMR",
    collectorNumber = "646",
    scryfallId = "ca318a56-f13e-46cb-819d-2cf8a2b16f0e",
    artist = "Seb McKinnon",
    imageUri = "https://cards.scryfall.io/normal/front/c/a/ca318a56-f13e-46cb-819d-2cf8a2b16f0e.jpg?1608918563",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
