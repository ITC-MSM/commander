package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Shipwreck Dowser reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M21's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ShipwreckDowserReprint = Printing(
    oracleId = "dc3073fc-dc23-426e-b867-fee5dd99fe25",
    name = "Shipwreck Dowser",
    setCode = "FDN",
    collectorNumber = "596",
    artist = "Caroline Gariba",
    imageUri = "https://cards.scryfall.io/normal/front/1/f/1f20fe3d-792a-4030-a25c-e81b48b2bcb4.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
