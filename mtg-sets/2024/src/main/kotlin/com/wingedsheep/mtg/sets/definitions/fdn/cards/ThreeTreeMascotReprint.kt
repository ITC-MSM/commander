package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Three Tree Mascot reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ThreeTreeMascotReprint = Printing(
    oracleId = "6cb9d153-4eaa-4f32-8930-bd68cd99c128",
    name = "Three Tree Mascot",
    setCode = "FDN",
    collectorNumber = "682",
    scryfallId = "40b8bf3a-1cb5-4ce2-ac25-9410f17130de",
    artist = "Gina Matarazzo",
    imageUri = "https://cards.scryfall.io/normal/front/4/0/40b8bf3a-1cb5-4ce2-ac25-9410f17130de.jpg?1730491179",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
