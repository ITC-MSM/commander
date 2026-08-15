package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Seachrome Coast reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SeachromeCoastReprint = Printing(
    oracleId = "9e7a240d-dc33-47ac-9f17-77fab4c1c340",
    name = "Seachrome Coast",
    setCode = "BLC",
    collectorNumber = "328",
    scryfallId = "88ec4d3b-f1f1-489c-b41e-0206e5e59fbd",
    artist = "Mauricio Calle",
    imageUri = "https://cards.scryfall.io/normal/front/8/8/88ec4d3b-f1f1-489c-b41e-0206e5e59fbd.jpg?1721429829",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
