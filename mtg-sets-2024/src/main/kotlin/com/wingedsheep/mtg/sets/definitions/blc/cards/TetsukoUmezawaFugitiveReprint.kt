package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tetsuko Umezawa, Fugitive reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TetsukoUmezawaFugitiveReprint = Printing(
    oracleId = "ceeeacbc-01b0-4421-aaca-2ce6cdbe45d7",
    name = "Tetsuko Umezawa, Fugitive",
    setCode = "BLC",
    collectorNumber = "177",
    scryfallId = "21625c5c-7a9c-498d-afb6-ea57dea33fc6",
    artist = "Randy Vargas",
    imageUri = "https://cards.scryfall.io/normal/front/2/1/21625c5c-7a9c-498d-afb6-ea57dea33fc6.jpg?1721429058",
    releaseDate = "2024-08-02",
    rarity = Rarity.UNCOMMON,
)
