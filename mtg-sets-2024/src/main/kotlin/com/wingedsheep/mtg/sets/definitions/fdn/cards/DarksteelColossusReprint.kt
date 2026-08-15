package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Darksteel Colossus reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DST's `cards/` package (the card's earliest real printing). This file contributes only the
 * FDN-specific presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DarksteelColossusReprint = Printing(
    oracleId = "1b09d0cf-403c-4a15-aeee-602a1bdaf0c1",
    name = "Darksteel Colossus",
    setCode = "FDN",
    collectorNumber = "671",
    scryfallId = "70197723-c61b-4600-b61d-41380c8f3067",
    artist = "Carl Critchlow",
    imageUri = "https://cards.scryfall.io/normal/front/7/0/70197723-c61b-4600-b61d-41380c8f3067.jpg?1783908906",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)
