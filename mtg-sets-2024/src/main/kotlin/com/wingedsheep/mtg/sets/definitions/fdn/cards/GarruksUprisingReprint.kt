package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Garruk's Uprising reprint in Foundations (FDN).
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in
 * `definitions/m21/cards/GarruksUprising.kt`. This file contributes only the
 * FDN-specific presentation row — set, collector number, art — picked up
 * automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's
 * `printings`.
 */
val GarruksUprisingReprint = Printing(
    oracleId = "3127ae9b-a7a7-43ec-89d7-688f8445b33d",
    name = "Garruk's Uprising",
    setCode = "FDN",
    collectorNumber = "220",
    scryfallId = "4805c303-e73b-443b-a09f-49d2c2c88bb5",
    artist = "Wisnu Tan",
    imageUri = "https://cards.scryfall.io/normal/front/4/8/4805c303-e73b-443b-a09f-49d2c2c88bb5.jpg?1730489415",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
