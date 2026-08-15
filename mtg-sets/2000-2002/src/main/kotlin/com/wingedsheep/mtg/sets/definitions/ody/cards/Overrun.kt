package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Overrun reprint in ODY.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * TMP's `cards/` package (the card's earliest real printing). This file contributes only
 * the ODY-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OverrunReprint = Printing(
    oracleId = "204f9afe-c20b-4933-b5cd-aa572784762a",
    name = "Overrun",
    setCode = "ODY",
    collectorNumber = "260",
    artist = "Carl Critchlow",
    imageUri = "https://cards.scryfall.io/normal/front/9/6/96f9df13-3bd8-415d-b949-9b39f97fdde7.jpg",
    releaseDate = "2001-10-01",
    rarity = Rarity.UNCOMMON,
)
