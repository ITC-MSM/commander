package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swiftwater Cliffs reprint in DFT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DFT-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SwiftwaterCliffsReprint = Printing(
    oracleId = "2f4ad084-2062-44c0-9975-15f100204531",
    name = "Swiftwater Cliffs",
    setCode = "DFT",
    collectorNumber = "265",
    scryfallId = "949bf6bb-1e97-48a3-8547-0a780664c275",
    artist = "Mark Poole",
    imageUri = "https://cards.scryfall.io/normal/front/9/4/949bf6bb-1e97-48a3-8547-0a780664c275.jpg?1738356952",
    releaseDate = "2025-02-14",
    rarity = Rarity.COMMON,
)
