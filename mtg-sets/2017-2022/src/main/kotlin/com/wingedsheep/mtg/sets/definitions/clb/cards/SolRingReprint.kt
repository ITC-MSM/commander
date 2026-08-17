package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sol Ring reprint in CLB. The canonical CardDefinition lives in
 * Limited Edition Alpha (`lea`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val SolRingReprint = Printing(
    oracleId = "6ad8011d-3471-4369-9d68-b264cc027487",
    name = "Sol Ring",
    setCode = "CLB",
    collectorNumber = "871",
    scryfallId = "199cde21-5bc3-49cd-acd4-bae3af6e5881",
    artist = "Mike Bierek",
    imageUri = "https://cards.scryfall.io/normal/front/1/9/199cde21-5bc3-49cd-acd4-bae3af6e5881.jpg?1783922385",
    releaseDate = "2022-06-10",
    rarity = Rarity.UNCOMMON,
)
