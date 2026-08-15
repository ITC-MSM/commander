package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Temporal Cleansing reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TemporalCleansingReprint = Printing(
    oracleId = "5f67698a-28db-43cd-aaf1-52371b0b47eb",
    name = "Temporal Cleansing",
    setCode = "MOM",
    collectorNumber = "80",
    scryfallId = "6e67031a-8216-4c66-b6fb-6628bd02d279",
    artist = "Dominik Mayer",
    imageUri = "https://cards.scryfall.io/normal/front/6/e/6e67031a-8216-4c66-b6fb-6628bd02d279.jpg?1682203448",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
