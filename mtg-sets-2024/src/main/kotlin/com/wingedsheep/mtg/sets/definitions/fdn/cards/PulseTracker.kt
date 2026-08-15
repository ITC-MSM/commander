package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Pulse Tracker reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * WWK's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PulseTrackerReprint = Printing(
    oracleId = "f916f689-a314-4952-8107-41c4c2f2e811",
    name = "Pulse Tracker",
    setCode = "FDN",
    collectorNumber = "755",
    artist = "Andrew Robinson",
    imageUri = "https://cards.scryfall.io/normal/front/7/b/7bc1441e-7d75-4a74-8733-19077dfa69b2.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
