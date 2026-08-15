package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Garruk's Uprising reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in
 * `definitions/m21/cards/GarruksUprising.kt`. This file contributes only the
 * BLC-specific presentation row — set, collector number, art — picked up
 * automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's
 * `printings`.
 */
val GarruksUprisingReprint = Printing(
    oracleId = "3127ae9b-a7a7-43ec-89d7-688f8445b33d",
    name = "Garruk's Uprising",
    setCode = "BLC",
    collectorNumber = "219",
    scryfallId = "0d229647-ffe9-4428-b4d6-a318108fc550",
    artist = "Wisnu Tan",
    imageUri = "https://cards.scryfall.io/normal/front/0/d/0d229647-ffe9-4428-b4d6-a318108fc550.jpg?1721429279",
    releaseDate = "2024-08-02",
    rarity = Rarity.UNCOMMON,
)
