package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Brineborn Cutthroat reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in M20's
 * `cards/` package (the card's earliest real printing). This file contributes only the
 * FDN-specific presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BrinebornCutthroatReprint = Printing(
    oracleId = "916cb70f-3b06-48ed-972d-75f805aa0892",
    name = "Brineborn Cutthroat",
    setCode = "FDN",
    collectorNumber = "152",
    scryfallId = "acf7aafb-931f-49e5-8691-eab8cb34b05e",
    artist = "Caio Monteiro",
    imageUri = "https://cards.scryfall.io/normal/front/a/c/acf7aafb-931f-49e5-8691-eab8cb34b05e.jpg?1782689135",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
