package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Essence Scatter reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EssenceScatterReprint = Printing(
    oracleId = "46665089-aa3d-44c3-964d-6638dfbb5782",
    name = "Essence Scatter",
    setCode = "FDN",
    collectorNumber = "153",
    scryfallId = "dd05c850-f91e-4ffb-b4cc-8418d49dad90",
    artist = "Josh Hass",
    imageUri = "https://cards.scryfall.io/normal/front/d/d/dd05c850-f91e-4ffb-b4cc-8418d49dad90.jpg?1730489174",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
