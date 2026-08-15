package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dry Spell reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * HML's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DrySpellReprint = Printing(
    oracleId = "980a9957-6b52-45c6-b847-f84974d5a653",
    name = "Dry Spell",
    setCode = "POR",
    collectorNumber = "90",
    artist = "Roger Raupp",
    imageUri = "https://cards.scryfall.io/normal/front/a/1/a142f369-8fdd-4dc8-b5d9-3493455cc588.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.UNCOMMON,
)
