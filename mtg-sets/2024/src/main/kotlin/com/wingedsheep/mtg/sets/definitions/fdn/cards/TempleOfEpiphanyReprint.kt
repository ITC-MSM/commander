package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Temple of Epiphany reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TempleOfEpiphanyReprint = Printing(
    oracleId = "79f94050-d850-41ca-b1db-5ae0cf743f0a",
    name = "Temple of Epiphany",
    setCode = "FDN",
    collectorNumber = "699",
    scryfallId = "224e8255-7bc6-44a2-86af-14f8446f4f77",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/2/2/224e8255-7bc6-44a2-86af-14f8446f4f77.jpg?1730491248",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
