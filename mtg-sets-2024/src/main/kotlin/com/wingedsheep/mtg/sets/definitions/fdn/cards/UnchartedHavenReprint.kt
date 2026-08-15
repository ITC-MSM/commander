package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Uncharted Haven reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val UnchartedHavenReprint = Printing(
    oracleId = "d23c3613-bc5e-4fc5-939c-62a090c53a79",
    name = "Uncharted Haven",
    setCode = "FDN",
    collectorNumber = "564",
    scryfallId = "172cd5b7-98fc-4add-b858-a0b3dfb75c19",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/1/7/172cd5b7-98fc-4add-b858-a0b3dfb75c19.jpg?1730490745",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
