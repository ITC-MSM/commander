package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bog Imp reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DRK's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BogImpReprint = Printing(
    oracleId = "45b94e3c-a905-435b-aee5-bec9239fd24c",
    name = "Bog Imp",
    setCode = "POR",
    collectorNumber = "81",
    artist = "Christopher Rush",
    imageUri = "https://cards.scryfall.io/normal/front/8/6/8681b3fd-33e5-4a45-8650-a4a142405096.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
