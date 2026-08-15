package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Doomed Traveler reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ISD's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DoomedTravelerReprint = Printing(
    oracleId = "a30907c0-fbde-4fd3-a8c7-f304305fcea7",
    name = "Doomed Traveler",
    setCode = "CMR",
    collectorNumber = "19",
    artist = "Lars Grant-West",
    imageUri = "https://cards.scryfall.io/normal/front/5/a/5a4573af-feba-4c9d-b24b-3d15888a5ce2.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
