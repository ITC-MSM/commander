package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dark Heart of the Wood reprint in RAV.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in DRK's `cards/` package
 * (the card's earliest real printing). This file contributes only the RAV-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DarkHeartOfTheWoodReprint = Printing(
    oracleId = "c44f40da-867e-4237-b4b1-ed6feb1f37b7",
    name = "Dark Heart of the Wood",
    setCode = "RAV",
    collectorNumber = "200",
    scryfallId = "baa3ae99-a770-4487-8de6-68a347ee64bb",
    artist = "Mark Tedin",
    imageUri = "https://cards.scryfall.io/normal/front/b/a/baa3ae99-a770-4487-8de6-68a347ee64bb.jpg?1783943623",
    releaseDate = "2005-10-07",
    rarity = Rarity.UNCOMMON,
)
