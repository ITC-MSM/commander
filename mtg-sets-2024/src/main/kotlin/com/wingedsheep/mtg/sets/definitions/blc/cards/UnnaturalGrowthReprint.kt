package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Unnatural Growth reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in
 * `definitions/mid/cards/UnnaturalGrowth.kt`. This file contributes only the
 * BLC-specific presentation row — set, collector number, art — picked up
 * automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's
 * `printings`.
 */
val UnnaturalGrowthReprint = Printing(
    oracleId = "7324abaa-48da-439d-9339-b0ea5eea612e",
    name = "Unnatural Growth",
    setCode = "BLC",
    collectorNumber = "245",
    scryfallId = "5a9d47cc-157b-4070-ae00-da1c5d7b94de",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/5/a/5a9d47cc-157b-4070-ae00-da1c5d7b94de.jpg?1721429426",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
