package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Outpost Siege reprint in Bloomburrow Commander (BLC). Canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in
 * [com.wingedsheep.mtg.sets.definitions.frf.cards.OutpostSiege]; this file
 * contributes only printing metadata.
 */
val OutpostSiegeReprint = Printing(
    oracleId = "ebb24fc7-dc71-4712-8c2a-b5920f78e55d",
    name = "Outpost Siege",
    setCode = "BLC",
    collectorNumber = "199",
    scryfallId = "51f8c63e-8778-43c5-9093-f419442295cf",
    artist = "Daarken",
    imageUri = "https://cards.scryfall.io/normal/front/5/1/51f8c63e-8778-43c5-9093-f419442295cf.jpg?1721429170",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
