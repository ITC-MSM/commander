package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Prying Eyes reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RNA's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PryingEyesReprint = Printing(
    oracleId = "06b60766-a1fb-4ed0-93f6-8423008f75b4",
    name = "Prying Eyes",
    setCode = "CMR",
    collectorNumber = "86",
    artist = "Magali Villeneuve",
    imageUri = "https://cards.scryfall.io/normal/front/e/b/eb58d7ba-ba86-433e-8f1e-3f492c380796.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
