package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rapacious Dragon reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M20's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RapaciousDragonReprint = Printing(
    oracleId = "0944ec2e-1dd9-459f-8f1d-667242cf52fe",
    name = "Rapacious Dragon",
    setCode = "FDN",
    collectorNumber = "544",
    artist = "Johan Grenier",
    imageUri = "https://cards.scryfall.io/normal/front/5/e/5eaddac1-d8ca-4949-8140-c9193e3df921.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
