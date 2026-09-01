package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mirror Entity reprint in VOC. The canonical CardDefinition lives in
 * Lorwyn (`lrw`), the card's earliest real printing; this file contributes
 * only per-printing presentation data.
 */
val MirrorEntityReprint = Printing(
    oracleId = "17e905ca-c0bd-473d-95a7-e180ba5fea43",
    name = "Mirror Entity",
    setCode = "VOC",
    collectorNumber = "94",
    scryfallId = "6733ee04-a282-43ea-830f-1ba806331c7b",
    artist = "Zoltan Boros & Gabor Szikszai",
    imageUri = "https://cards.scryfall.io/normal/front/6/7/6733ee04-a282-43ea-830f-1ba806331c7b.jpg?1783924970",
    releaseDate = "2021-11-19",
    rarity = Rarity.RARE,
)
