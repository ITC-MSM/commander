package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Arcane Sanctum reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Shards of Alara's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val ArcaneSanctumReprint = Printing(
    oracleId = "7d7cf15c-06b9-4062-a1eb-32614c458a3b",
    name = "Arcane Sanctum",
    setCode = "NCC",
    collectorNumber = "385",
    scryfallId = "10ed5393-e274-4412-ba5d-6faecf3c18d8",
    artist = "Anthony Francisco",
    imageUri = "https://cards.scryfall.io/normal/front/1/0/10ed5393-e274-4412-ba5d-6faecf3c18d8.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
