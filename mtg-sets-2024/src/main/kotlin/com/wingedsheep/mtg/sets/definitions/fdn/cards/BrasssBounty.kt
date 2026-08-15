package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Brass's Bounty reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BrasssBountyReprint = Printing(
    oracleId = "63b0538e-aa94-4e23-adf1-704cbf5bc3e5",
    name = "Brass's Bounty",
    setCode = "FDN",
    collectorNumber = "190",
    artist = "Grzegorz Rutkowski",
    imageUri = "https://cards.scryfall.io/normal/front/6/5/65fe7127-b0ec-400f-97f1-6e17ab8e319d.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
