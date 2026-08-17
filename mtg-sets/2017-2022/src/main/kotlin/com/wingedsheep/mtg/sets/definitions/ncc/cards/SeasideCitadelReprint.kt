package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Seaside Citadel reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Shards of Alara's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val SeasideCitadelReprint = Printing(
    oracleId = "2ae77795-6a80-498b-bf69-6fd612f601e4",
    name = "Seaside Citadel",
    setCode = "NCC",
    collectorNumber = "425",
    scryfallId = "816ce96f-9180-4f95-a7cd-334f4a159fa7",
    artist = "Volkan Baǵa",
    imageUri = "https://cards.scryfall.io/normal/front/8/1/816ce96f-9180-4f95-a7cd-334f4a159fa7.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
