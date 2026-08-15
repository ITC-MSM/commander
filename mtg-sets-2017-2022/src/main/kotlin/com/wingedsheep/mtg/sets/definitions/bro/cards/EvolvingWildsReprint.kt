package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Evolving Wilds reprint in BRO.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BRO-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EvolvingWildsReprint = Printing(
    oracleId = "a75445d3-1303-4bb5-89ad-26ea93fecd48",
    name = "Evolving Wilds",
    setCode = "BRO",
    collectorNumber = "261",
    scryfallId = "238de888-b1bd-4cce-9aa2-d0dd69ae7f0d",
    artist = "Sam Burley",
    imageUri = "https://cards.scryfall.io/normal/front/2/3/238de888-b1bd-4cce-9aa2-d0dd69ae7f0d.jpg?1674422191",
    releaseDate = "2022-11-18",
    rarity = Rarity.COMMON,
)
