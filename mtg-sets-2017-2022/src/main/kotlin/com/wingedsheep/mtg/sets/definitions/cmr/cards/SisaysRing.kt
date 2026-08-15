package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sisay's Ring reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SisaysRingReprint = Printing(
    oracleId = "8f5822ae-651f-410e-9316-5522eeb52d72",
    name = "Sisay's Ring",
    setCode = "CMR",
    collectorNumber = "340",
    artist = "Donato Giancola",
    imageUri = "https://cards.scryfall.io/normal/front/2/0/20c0e608-0208-408a-b473-1e54caa96cea.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
