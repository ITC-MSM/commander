package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Smuggler reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in M20's
 * `cards/` package (the card's earliest real printing). This file contributes only the
 * FDN-specific presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GoblinSmugglerReprint = Printing(
    oracleId = "eae15f7d-1f6e-4dfc-afea-7698b93cd145",
    name = "Goblin Smuggler",
    setCode = "FDN",
    collectorNumber = "540",
    scryfallId = "5837ccf1-9bac-4afb-bcc9-82c5f3c0e8e2",
    artist = "Milivoj Ćeran",
    imageUri = "https://cards.scryfall.io/normal/front/5/8/5837ccf1-9bac-4afb-bcc9-82c5f3c0e8e2.jpg?1782688794",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
