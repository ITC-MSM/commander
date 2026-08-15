package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Game Trail reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in
 * `definitions/soi/cards/GameTrail.kt`. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GameTrailReprint = Printing(
    oracleId = "00de57d2-7cb6-4337-9bc6-f6711e4dfabf",
    name = "Game Trail",
    setCode = "BLC",
    collectorNumber = "306",
    scryfallId = "e42e3260-33d7-4cee-8cac-61ed006abf3e",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/e/4/e42e3260-33d7-4cee-8cac-61ed006abf3e.jpg?1721429732",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
