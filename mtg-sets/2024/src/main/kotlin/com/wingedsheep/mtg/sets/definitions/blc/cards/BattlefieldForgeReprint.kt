package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Battlefield Forge reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BattlefieldForgeReprint = Printing(
    oracleId = "6b75b94e-83b7-457e-ac41-7ca90b5a59aa",
    name = "Battlefield Forge",
    setCode = "BLC",
    collectorNumber = "293",
    scryfallId = "24323a73-8c9a-4cd4-a98f-9ea0d1f33bf6",
    artist = "Darrell Riche",
    imageUri = "https://cards.scryfall.io/normal/front/2/4/24323a73-8c9a-4cd4-a98f-9ea0d1f33bf6.jpg?1721429674",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
