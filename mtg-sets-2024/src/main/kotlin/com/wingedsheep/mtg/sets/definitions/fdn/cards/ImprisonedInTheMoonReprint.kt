package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Imprisoned in the Moon reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in Eldritch
 * Moon's `cards/` package — see
 * `com.wingedsheep.mtg.sets.definitions.emn.cards.ImprisonedInTheMoon`. This file contributes
 * only the FDN-specific presentation row — set, collector number, art — picked up
 * automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ImprisonedInTheMoonReprint = Printing(
    oracleId = "339a7418-9a29-4eae-a3c2-ea590d175936",
    name = "Imprisoned in the Moon",
    setCode = "FDN",
    collectorNumber = "156",
    scryfallId = "ee28e147-6622-4399-a314-c14a5c912dd0",
    artist = "Ryan Alexander Lee",
    imageUri = "https://cards.scryfall.io/normal/front/e/e/ee28e147-6622-4399-a314-c14a5c912dd0.jpg?1782689132",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
