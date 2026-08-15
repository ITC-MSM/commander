package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Volunteer Militia reprint in PTK.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * P02's `cards/` package (the card's earliest real printing). This file
 * contributes only the PTK-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VolunteerMilitiaReprint = Printing(
    oracleId = "4f598459-b776-4ac1-bc69-8fde21674451",
    name = "Volunteer Militia",
    setCode = "PTK",
    collectorNumber = "30",
    artist = "Lin Yan",
    imageUri = "https://cards.scryfall.io/normal/front/0/a/0af243f6-ef28-49d1-afeb-ac03d568ed6a.jpg?1783946126",
    releaseDate = "1999-05-01",
    rarity = Rarity.COMMON,
)
