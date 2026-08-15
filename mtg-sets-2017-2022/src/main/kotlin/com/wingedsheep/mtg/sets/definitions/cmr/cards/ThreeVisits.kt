package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Three Visits reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * PTK's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ThreeVisitsReprint = Printing(
    oracleId = "1b882a0e-0ede-4d1a-bd1a-9b7cffbcde8e",
    name = "Three Visits",
    setCode = "CMR",
    collectorNumber = "261",
    artist = "Yeong-Hao Han",
    imageUri = "https://cards.scryfall.io/normal/front/b/d/bd9307ec-987b-4bbf-a679-cfaabe283a80.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
