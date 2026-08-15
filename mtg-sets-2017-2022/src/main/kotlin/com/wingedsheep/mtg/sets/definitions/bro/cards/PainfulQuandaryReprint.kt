package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Painful Quandary reprints in The Brothers' War.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Scars of Mirrodin's
 * `cards/` package (its earliest real printing). These files contribute only the BRO
 * presentation rows, picked up automatically by `CardDiscovery.findPrintingsIn`:
 *  - collector 111 — the main-set printing.
 *  - collector 331 — the extended-art variant.
 */
val PainfulQuandaryBro = Printing(
    oracleId = "c37051cc-6683-4dbb-b5ff-5c3a5bdab1df",
    name = "Painful Quandary",
    setCode = "BRO",
    collectorNumber = "111",
    scryfallId = "bed5e030-874f-4a00-8544-78ba04033f53",
    artist = "David Palumbo",
    imageUri = "https://cards.scryfall.io/normal/front/b/e/bed5e030-874f-4a00-8544-78ba04033f53.jpg?1782699785",
    releaseDate = "2022-11-18",
    rarity = Rarity.RARE,
)

val PainfulQuandaryBroExtended = Printing(
    oracleId = "c37051cc-6683-4dbb-b5ff-5c3a5bdab1df",
    name = "Painful Quandary",
    setCode = "BRO",
    collectorNumber = "331",
    scryfallId = "c97467f7-01a9-46c8-8e1c-7ebc5f65a0c1",
    artist = "David Palumbo",
    imageUri = "https://cards.scryfall.io/normal/front/c/9/c97467f7-01a9-46c8-8e1c-7ebc5f65a0c1.jpg?1782699639",
    releaseDate = "2022-11-18",
    rarity = Rarity.RARE,
    frameEffects = listOf("extendedart"),
)
