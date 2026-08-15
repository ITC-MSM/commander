package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Reclamation Sage reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M15's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ReclamationSageReprint = Printing(
    oracleId = "032ec6e2-6cc3-4a97-9cc7-3233f5e11904",
    name = "Reclamation Sage",
    setCode = "CMR",
    collectorNumber = "248",
    artist = "Christopher Moeller",
    imageUri = "https://cards.scryfall.io/normal/front/b/8/b800bb44-3594-4fb7-b007-10732632b51a.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
