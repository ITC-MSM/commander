package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cloudblazer reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * KLD's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CloudblazerReprint = Printing(
    oracleId = "f84d1291-1f82-4b67-a26e-b79624b4ce1d",
    name = "Cloudblazer",
    setCode = "FDN",
    collectorNumber = "653",
    artist = "Dan Murayama Scott",
    imageUri = "https://cards.scryfall.io/normal/front/a/9/a9c966ab-0d92-4fa7-8a91-1f77089d8fc7.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
