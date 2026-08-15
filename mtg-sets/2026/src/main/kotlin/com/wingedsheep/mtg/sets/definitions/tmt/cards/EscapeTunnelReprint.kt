package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Escape Tunnel reprint in TMT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in MKM. This file
 * contributes only the TMT-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the
 * set's `printings`.
 */
val EscapeTunnelReprint = Printing(
    oracleId = "0056fc91-4398-471c-b561-7ff99750ac8a",
    name = "Escape Tunnel",
    setCode = "TMT",
    collectorNumber = "184",
    scryfallId = "5df90940-15ea-418c-8547-6c75d69ec6d3",
    artist = "Aenami",
    imageUri = "https://cards.scryfall.io/normal/front/5/d/5df90940-15ea-418c-8547-6c75d69ec6d3.jpg?1771424732",
    releaseDate = "2026-03-06",
    rarity = Rarity.COMMON,
)
