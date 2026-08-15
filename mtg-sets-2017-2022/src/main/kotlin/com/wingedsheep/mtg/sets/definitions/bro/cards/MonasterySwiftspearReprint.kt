package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Monastery Swiftspear reprint in BRO.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BRO-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MonasterySwiftspearReprint = Printing(
    oracleId = "dafd2713-d1bc-474b-b390-d2ff20b5375e",
    name = "Monastery Swiftspear",
    setCode = "BRO",
    collectorNumber = "144",
    scryfallId = "d6bfa227-4309-40ed-952c-279595eab17e",
    artist = "Gabor Szikszai",
    imageUri = "https://cards.scryfall.io/normal/front/d/6/d6bfa227-4309-40ed-952c-279595eab17e.jpg?1701690543",
    releaseDate = "2022-11-18",
    rarity = Rarity.UNCOMMON,
)
