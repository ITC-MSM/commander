package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Nest Robber reprint in J22. Canonical CardDefinition lives in Ixalan (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.xln.cards.NestRobber`.
 */
val NestRobberReprint = Printing(
    oracleId = "b909ff27-423f-4e84-887b-7840149a85be",
    name = "Nest Robber",
    setCode = "J22",
    collectorNumber = "576",
    scryfallId = "3135dfde-ad52-4b93-9356-107d0efad93c",
    artist = "Jonathan Kuo",
    imageUri = "https://cards.scryfall.io/normal/front/3/1/3135dfde-ad52-4b93-9356-107d0efad93c.jpg?1783918921",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
