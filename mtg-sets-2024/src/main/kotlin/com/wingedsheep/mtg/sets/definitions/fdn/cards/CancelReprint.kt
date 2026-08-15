package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cancel reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CancelReprint = Printing(
    oracleId = "7d00fb28-ea6c-49a9-b4af-ffb38860a9a7",
    name = "Cancel",
    setCode = "FDN",
    collectorNumber = "505",
    scryfallId = "475bff39-220a-4490-9c2e-d311e306a6db",
    artist = "Mathias Kollros",
    imageUri = "https://cards.scryfall.io/normal/front/4/7/475bff39-220a-4490-9c2e-d311e306a6db.jpg?1730490517",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
