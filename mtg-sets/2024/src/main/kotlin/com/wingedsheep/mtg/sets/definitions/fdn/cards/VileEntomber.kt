package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vile Entomber reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MH2's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VileEntomberReprint = Printing(
    oracleId = "556dd333-4066-41f7-98f6-41794754de71",
    name = "Vile Entomber",
    setCode = "FDN",
    collectorNumber = "616",
    artist = "Chris Cold",
    imageUri = "https://cards.scryfall.io/normal/front/8/3/833b5a32-5d77-4e46-a524-25bada29ef59.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
