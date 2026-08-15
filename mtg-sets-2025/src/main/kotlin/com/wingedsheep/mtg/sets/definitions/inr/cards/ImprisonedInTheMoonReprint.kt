package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Imprisoned in the Moon reprint in INR (Innistrad Remastered).
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in Eldritch
 * Moon's `cards/` package — see
 * `com.wingedsheep.mtg.sets.definitions.emn.cards.ImprisonedInTheMoon`. This file contributes
 * only the INR-specific presentation row — set, collector number, art — picked up
 * automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ImprisonedInTheMoonReprint = Printing(
    oracleId = "339a7418-9a29-4eae-a3c2-ea590d175936",
    name = "Imprisoned in the Moon",
    setCode = "INR",
    collectorNumber = "69",
    scryfallId = "0d150547-09f5-45ce-a825-89944b066bd4",
    artist = "Ryan Alexander Lee",
    imageUri = "https://cards.scryfall.io/normal/front/0/d/0d150547-09f5-45ce-a825-89944b066bd4.jpg?1782726835",
    releaseDate = "2025-01-24",
    rarity = Rarity.COMMON,
)
