package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wind Drake reprint in Tempest.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Portal's
 * `cards/` package; this file contributes only the TMP-specific presentation row.
 */
val WindDrakeReprint = Printing(
    oracleId = "d6ffdaf0-ac08-4de9-bbce-2eab2f86bcca",
    name = "Wind Drake",
    setCode = "TMP",
    collectorNumber = "105",
    scryfallId = "91e0c9e2-a45d-44d1-b73e-73c0a22d0752",
    artist = "Greg Simanson",
    imageUri = "https://cards.scryfall.io/normal/front/9/1/91e0c9e2-a45d-44d1-b73e-73c0a22d0752.jpg?1562055424",
    releaseDate = "1997-10-14",
    rarity = Rarity.COMMON,
)
