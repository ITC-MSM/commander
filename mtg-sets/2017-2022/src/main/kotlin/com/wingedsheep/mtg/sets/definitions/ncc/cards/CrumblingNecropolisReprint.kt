package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Crumbling Necropolis reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Shards of Alara's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val CrumblingNecropolisReprint = Printing(
    oracleId = "7190debf-708b-4f41-9714-0d0a5bd5a74e",
    name = "Crumbling Necropolis",
    setCode = "NCC",
    collectorNumber = "397",
    scryfallId = "e0f698f6-28b6-499e-af63-c223c02c3b4b",
    artist = "Dave Kendall",
    imageUri = "https://cards.scryfall.io/normal/front/e/0/e0f698f6-28b6-499e-af63-c223c02c3b4b.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
