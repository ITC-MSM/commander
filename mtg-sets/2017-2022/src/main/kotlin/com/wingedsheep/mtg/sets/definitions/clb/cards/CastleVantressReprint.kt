package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Castle Vantress reprint in CLB. The canonical CardDefinition lives in
 * Throne of Eldraine (`eld`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val CastleVantressReprint = Printing(
    oracleId = "cdf41cf4-4e77-453d-be5b-0abbbd358934",
    name = "Castle Vantress",
    setCode = "CLB",
    collectorNumber = "885",
    scryfallId = "dcf70844-18b1-4046-ae1d-ef41790bdcde",
    artist = "John Avon",
    imageUri = "https://cards.scryfall.io/normal/front/d/c/dcf70844-18b1-4046-ae1d-ef41790bdcde.jpg?1783922379",
    releaseDate = "2022-06-10",
    rarity = Rarity.RARE,
)
