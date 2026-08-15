package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Blossoming Sands reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BlossomingSandsReprint = Printing(
    oracleId = "45429b2c-be3b-4b2e-9bab-a059ccbda8cd",
    name = "Blossoming Sands",
    setCode = "MOM",
    collectorNumber = "268",
    scryfallId = "e34684d6-2935-4776-9a86-b603ad8cf624",
    artist = "Robin Olausson",
    imageUri = "https://cards.scryfall.io/normal/front/e/3/e34684d6-2935-4776-9a86-b603ad8cf624.jpg?1682205840",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
