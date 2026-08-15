package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Stun reprint in Invasion.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Tempest's
 * `cards/` package (earliest real-expansion printing); this file contributes
 * only the INV-specific presentation row.
 */
val StunReprint = Printing(
    oracleId = "d3491972-44c5-4962-a680-42b79357a189",
    name = "Stun",
    setCode = "INV",
    collectorNumber = "172",
    scryfallId = "d22f3ae8-a40b-4dab-abf4-3ab7b05191f7",
    artist = "Mike Ploog",
    imageUri = "https://cards.scryfall.io/normal/front/d/2/d22f3ae8-a40b-4dab-abf4-3ab7b05191f7.jpg?1562937241",
    releaseDate = "2000-10-02",
    rarity = Rarity.COMMON,
)
