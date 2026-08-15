package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dismal Backwater reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DismalBackwaterReprint = Printing(
    oracleId = "865a2194-fca0-446e-aae3-ca475cd66e00",
    name = "Dismal Backwater",
    setCode = "MOM",
    collectorNumber = "269",
    scryfallId = "33cd4f63-3484-4cee-8603-1f89cabee6c3",
    artist = "Chris Ostrowski",
    imageUri = "https://cards.scryfall.io/normal/front/3/3/33cd4f63-3484-4cee-8603-1f89cabee6c3.jpg?1682205851",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
