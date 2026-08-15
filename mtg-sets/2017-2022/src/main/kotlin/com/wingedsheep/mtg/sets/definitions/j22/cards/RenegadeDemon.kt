package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Renegade Demon reprint in J22.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * AVR's `cards/` package (the card's earliest real printing). This file
 * contributes only the J22-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RenegadeDemonReprint = Printing(
    oracleId = "5f444efc-3bc2-4ca8-986c-f804f9e1a95e",
    name = "Renegade Demon",
    setCode = "J22",
    collectorNumber = "126",
    artist = "Alexandre Honoré",
    imageUri = "https://cards.scryfall.io/normal/front/2/c/2cee747f-4a9e-4752-815a-dc7ebabccd6b.jpg?1783919142",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
