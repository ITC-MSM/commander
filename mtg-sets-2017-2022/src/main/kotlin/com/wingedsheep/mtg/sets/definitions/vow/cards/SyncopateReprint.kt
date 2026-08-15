package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Syncopate reprint in VOW.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the VOW-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SyncopateReprint = Printing(
    oracleId = "318f7f70-e374-40ef-8afb-3389c10461d8",
    name = "Syncopate",
    setCode = "VOW",
    collectorNumber = "83",
    scryfallId = "08375017-4432-4296-9799-966db145ed7c",
    artist = "Marta Nael",
    imageUri = "https://cards.scryfall.io/normal/front/0/8/08375017-4432-4296-9799-966db145ed7c.jpg?1643588741",
    releaseDate = "2021-11-19",
    rarity = Rarity.COMMON,
)
