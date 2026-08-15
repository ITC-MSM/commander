package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Skyraker Giant reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ORI's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SkyrakerGiantReprint = Printing(
    oracleId = "d481630c-83c4-47d2-a770-db331154d2b9",
    name = "Skyraker Giant",
    setCode = "CMR",
    collectorNumber = "199",
    artist = "Anastasia Ovchinnikova",
    imageUri = "https://cards.scryfall.io/normal/front/7/1/71eed5cc-e5e8-476d-bdd7-c51233359a48.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
