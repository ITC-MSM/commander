package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Eyeblight Massacre reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ORI's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EyeblightMassacreReprint = Printing(
    oracleId = "3b90dd70-798e-4dc4-99e7-b36d6823af2b",
    name = "Eyeblight Massacre",
    setCode = "CMR",
    collectorNumber = "125",
    artist = "Igor Kieryluk",
    imageUri = "https://cards.scryfall.io/normal/front/b/e/be00670b-64a8-428a-8db0-9119efddaa1b.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
