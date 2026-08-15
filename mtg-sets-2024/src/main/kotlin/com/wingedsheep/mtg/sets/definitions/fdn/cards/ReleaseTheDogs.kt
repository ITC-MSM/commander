package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Release the Dogs reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * JMP's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ReleaseTheDogsReprint = Printing(
    oracleId = "b97d21b4-d730-479f-872f-3e1645b66751",
    name = "Release the Dogs",
    setCode = "FDN",
    collectorNumber = "580",
    artist = "Jason Kang",
    imageUri = "https://cards.scryfall.io/normal/front/0/7/07f10906-4fd5-44e8-99c7-3f9dcc988c48.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
