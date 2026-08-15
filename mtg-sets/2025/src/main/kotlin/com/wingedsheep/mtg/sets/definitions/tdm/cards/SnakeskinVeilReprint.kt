package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Snakeskin Veil reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SnakeskinVeilReprint = Printing(
    oracleId = "1e6a24be-8281-41c1-a5ba-b68f0ef1d7b8",
    name = "Snakeskin Veil",
    setCode = "TDM",
    collectorNumber = "159",
    scryfallId = "a3d2c692-7566-468e-9c86-47a9f768fde2",
    artist = "Monztre",
    imageUri = "https://cards.scryfall.io/normal/front/a/3/a3d2c692-7566-468e-9c86-47a9f768fde2.jpg?1743204601",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
