package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Helm of the Host reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HelmOfTheHostReprint = Printing(
    oracleId = "83b43aba-bf9c-4da2-967d-9daa632e97d2",
    name = "Helm of the Host",
    setCode = "BLC",
    collectorNumber = "276",
    scryfallId = "898c66d5-d8e6-4a58-ba41-4650ba0e7262",
    artist = "Igor Kieryluk",
    imageUri = "https://cards.scryfall.io/normal/front/8/9/898c66d5-d8e6-4a58-ba41-4650ba0e7262.jpg?1721429592",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
