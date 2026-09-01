package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mirror Entity reprint in CLB. The canonical CardDefinition lives in
 * Lorwyn (`lrw`), the card's earliest real printing; this file contributes
 * only per-printing presentation data.
 */
val MirrorEntityReprint = Printing(
    oracleId = "17e905ca-c0bd-473d-95a7-e180ba5fea43",
    name = "Mirror Entity",
    setCode = "CLB",
    collectorNumber = "701",
    scryfallId = "3d9149ed-0e59-48b3-b48c-d5ea77b7239e",
    artist = "Zoltan Boros & Gabor Szikszai",
    imageUri = "https://cards.scryfall.io/normal/front/3/d/3d9149ed-0e59-48b3-b48c-d5ea77b7239e.jpg?1783922484",
    releaseDate = "2022-06-10",
    rarity = Rarity.RARE,
)
