package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Negate reprint in TMT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in the Foundations
 * (FDN) package. This file contributes only the TMT-specific presentation row —
 * set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val NegateReprint = Printing(
    oracleId = "3407fe41-fdd3-4119-8f70-4bc4590a379f",
    name = "Negate",
    setCode = "TMT",
    collectorNumber = "47",
    scryfallId = "52d58fe4-6070-4022-9cd7-c35a11b44525",
    artist = "Ryan Valle",
    imageUri = "https://cards.scryfall.io/normal/front/5/2/52d58fe4-6070-4022-9cd7-c35a11b44525.jpg?1771342360",
    releaseDate = "2026-03-06",
    rarity = Rarity.COMMON,
)
