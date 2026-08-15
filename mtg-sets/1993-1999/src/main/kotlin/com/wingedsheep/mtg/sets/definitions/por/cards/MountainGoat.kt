package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mountain Goat reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ICE's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MountainGoatReprint = Printing(
    oracleId = "031cd561-d9b1-4a0e-bc38-bc09e80563c3",
    name = "Mountain Goat",
    setCode = "POR",
    collectorNumber = "141",
    artist = "Una Fricker",
    imageUri = "https://cards.scryfall.io/normal/front/3/2/325100f1-d424-4db0-bfa9-24877156c0ba.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.UNCOMMON,
)
