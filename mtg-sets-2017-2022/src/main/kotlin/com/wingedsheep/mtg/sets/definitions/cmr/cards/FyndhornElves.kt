package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fyndhorn Elves reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ICE's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FyndhornElvesReprint = Printing(
    oracleId = "df317532-7d36-40fd-938f-e972749c8792",
    name = "Fyndhorn Elves",
    setCode = "CMR",
    collectorNumber = "228",
    artist = "Igor Kieryluk",
    imageUri = "https://cards.scryfall.io/normal/front/4/5/450744cf-7eba-491b-97b0-ca80c6368bbb.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
