package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Man-o'-War reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ManOWarReprint = Printing(
    oracleId = "67a3541c-8408-40c8-b44f-90035b860f57",
    name = "Man-o'-War",
    setCode = "POR",
    collectorNumber = "59",
    artist = "Una Fricker",
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e835b618-83c1-46e2-b8bd-aec56f58ccfc.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.UNCOMMON,
)
