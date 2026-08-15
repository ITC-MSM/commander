package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sol Ring reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SolRingReprint = Printing(
    oracleId = "6ad8011d-3471-4369-9d68-b264cc027487",
    name = "Sol Ring",
    setCode = "CMR",
    collectorNumber = "472",
    artist = "Mike Bierek",
    imageUri = "https://cards.scryfall.io/normal/front/5/8/58b26011-e103-45c4-a253-900f4e6b2eeb.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
