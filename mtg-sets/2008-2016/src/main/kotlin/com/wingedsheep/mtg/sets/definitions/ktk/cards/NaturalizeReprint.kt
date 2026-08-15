package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Naturalize reprint in Khans of Tarkir.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in another set's package;
 * this file contributes only the KTK-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via
 * `KhansOfTarkirSet.printings`.
 */
val NaturalizeReprint = Printing(
    oracleId = "bdb3ca68-ec1f-4e16-81cc-d23f8f52c728",
    name = "Naturalize",
    setCode = "KTK",
    collectorNumber = "142",
    scryfallId = "b129b44e-a1ce-41f2-a0cf-b6c879c7cbbd",
    artist = "James Paick",
    imageUri = "https://cards.scryfall.io/normal/front/b/1/b129b44e-a1ce-41f2-a0cf-b6c879c7cbbd.jpg?1562792044",
    releaseDate = "2014-09-26",
    rarity = Rarity.COMMON,
)
