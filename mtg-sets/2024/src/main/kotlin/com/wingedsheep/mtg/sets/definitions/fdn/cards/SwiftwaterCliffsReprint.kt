package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swiftwater Cliffs reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SwiftwaterCliffsReprint = Printing(
    oracleId = "2f4ad084-2062-44c0-9975-15f100204531",
    name = "Swiftwater Cliffs",
    setCode = "FDN",
    collectorNumber = "268",
    scryfallId = "fb88667d-7088-4889-960f-317486ebe856",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/f/b/fb88667d-7088-4889-960f-317486ebe856.jpg?1730489604",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
