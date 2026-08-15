package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cast Down reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DOM's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CastDownReprint = Printing(
    oracleId = "cdaab6b0-1a2d-4809-8e6b-56013acd8f78",
    name = "Cast Down",
    setCode = "CMR",
    collectorNumber = "112",
    artist = "Bastien L. Deharme",
    imageUri = "https://cards.scryfall.io/normal/front/2/1/21c8426e-476a-45e4-b3a9-841da54d966c.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
